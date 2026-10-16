# Fit10

Offline-first Android workout app (being pivoted to Fit10 Mom, a postpartum workout app).
Build with JDK 17: `./gradlew assembleDebug`.

## 16 KB page-size check

Google Play requires native libraries to support 16 KB memory pages on Android 15+.
`assembleDebug` is followed automatically by `:app:check16KbAlignment`, which runs
`scripts/check-16kb-alignment.sh` and fails the build if any `lib/*/*.so` in the debug APK
has an ELF LOAD segment aligned below `2**14` (checked with the NDK's `llvm-objdump`) or
fails `zipalign -c -P 16 -v 4` (build-tools 37.0.0).

Run it on its own:

```bash
./gradlew :app:check16KbAlignment          # builds the debug APK if needed
scripts/check-16kb-alignment.sh [path/to/app.apk]
```

The script finds the SDK via `ANDROID_HOME`, `ANDROID_SDK_ROOT`, or `sdk.dir` in
`local.properties`, uses the newest installed NDK, and prints a per-library pass/fail table.
If it fails after a dependency bump, upgrade or replace the library that shows `FAIL`.
