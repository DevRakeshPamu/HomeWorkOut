#!/usr/bin/env bash
# Verifies every native library in an APK is 16 KB page-size compatible
# (Google Play requirement for Android 15+):
#   1. every ELF LOAD segment is aligned to >= 2**14 (llvm-objdump from the NDK)
#   2. the .so is stored 16 KB-aligned in the APK (zipalign -c -P 16 -v 4)
#
# Usage: scripts/check-16kb-alignment.sh [path/to/app.apk]
# Default APK: app/build/outputs/apk/debug/app-debug.apk
# The SDK is found via ANDROID_HOME, ANDROID_SDK_ROOT, or sdk.dir in local.properties.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
APK="${1:-$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk}"
BUILD_TOOLS_VERSION="${BUILD_TOOLS_VERSION:-37.0.0}"
MIN_ALIGN_EXP=14

die() { echo "error: $*" >&2; exit 2; }

[[ -f "$APK" ]] || die "APK not found: $APK (run ./gradlew assembleDebug first)"

# --- Locate the SDK ---------------------------------------------------------
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [[ -z "$SDK" && -f "$ROOT_DIR/local.properties" ]]; then
    SDK="$(sed -n 's/^sdk\.dir=//p' "$ROOT_DIR/local.properties" | tail -n1)"
fi
[[ -n "$SDK" && -d "$SDK" ]] || die "Android SDK not found; set ANDROID_HOME or sdk.dir in local.properties"

ZIPALIGN="$SDK/build-tools/$BUILD_TOOLS_VERSION/zipalign"
[[ -x "$ZIPALIGN" ]] || die "zipalign not found at $ZIPALIGN"

NDK_VERSION="$(ls "$SDK/ndk" 2>/dev/null | sort -t. -k1,1n -k2,2n -k3,3n | tail -n1)"
[[ -n "$NDK_VERSION" ]] || die "no NDK installed under $SDK/ndk"
OBJDUMP="$(ls "$SDK/ndk/$NDK_VERSION"/toolchains/llvm/prebuilt/*/bin/llvm-objdump 2>/dev/null | head -n1)"
[[ -x "$OBJDUMP" ]] || die "llvm-objdump not found in NDK $NDK_VERSION"

# --- Gather data ------------------------------------------------------------
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

LIBS=()
while IFS= read -r lib; do LIBS+=("$lib"); done < <(
    unzip -Z1 "$APK" | grep -E '^lib/[^/]+/[^/]+\.so$' | sort
)

echo "APK:        $APK"
echo "NDK:        $NDK_VERSION"
echo "Build-tools $BUILD_TOOLS_VERSION"
echo

if [[ ${#LIBS[@]} -eq 0 ]]; then
    echo "No native libraries in APK; nothing to check."
    exit 0
fi

unzip -q "$APK" 'lib/*' -d "$WORK"

# zipalign prints one line per entry, e.g. "  123456 lib/x86/libfoo.so (OK)"
# or "(BAD - 4096)". Its exit status is the overall verdict.
ZIP_OUT="$WORK/zipalign.txt"
ZIP_STATUS=0
"$ZIPALIGN" -c -P 16 -v 4 "$APK" >"$ZIP_OUT" 2>&1 || ZIP_STATUS=$?

FAILED=0
printf '%-48s %-10s %-22s %s\n' "LIBRARY" "ELF ALIGN" "ZIPALIGN" "RESULT"
printf '%-48s %-10s %-22s %s\n' "-------" "---------" "--------" "------"
for lib in "${LIBS[@]}"; do
    # Smallest LOAD-segment alignment exponent, from lines like "LOAD off ... align 2**14".
    min_exp="$("$OBJDUMP" -p "$WORK/$lib" | awk '
        $1 == "LOAD" { for (i = 1; i <= NF; i++) if ($i == "align") { split($(i+1), a, "\\*\\*"); e = a[2] + 0; if (min == "" || e < min) min = e } }
        END { print min }')"
    zip_line="$(grep -F " $lib " "$ZIP_OUT" | head -n1 || true)"
    zip_state="$(sed -n 's/.*(\(.*\))$/\1/p' <<<"$zip_line")"

    elf_ok=0; zip_ok=0
    [[ -n "$min_exp" && "$min_exp" -ge $MIN_ALIGN_EXP ]] && elf_ok=1
    [[ "$zip_state" == OK* ]] && zip_ok=1

    result="PASS"
    if [[ $elf_ok -eq 0 || $zip_ok -eq 0 ]]; then result="FAIL"; FAILED=$((FAILED + 1)); fi
    printf '%-48s %-10s %-22s %s\n' "$lib" "2**${min_exp:-?}" "${zip_state:-missing}" "$result"
done
echo

if [[ $ZIP_STATUS -ne 0 && $FAILED -eq 0 ]]; then
    echo "zipalign -c -P 16 -v 4 failed for a non-library entry:"
    grep -F 'BAD' "$ZIP_OUT" || tail -n5 "$ZIP_OUT"
    FAILED=1
fi

if [[ $FAILED -ne 0 ]]; then
    echo "FAIL: native libraries are not 16 KB page-size compatible (need LOAD align >= 2**$MIN_ALIGN_EXP and zipalign OK)."
    exit 1
fi
echo "PASS: all ${#LIBS[@]} native libraries are 16 KB page-size compatible."
