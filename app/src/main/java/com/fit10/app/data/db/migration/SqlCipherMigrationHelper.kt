package com.fit10.app.data.db.migration

import android.content.Context
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.io.File
import java.io.FileInputStream

/**
 * Handles seamless, zero-data-loss migration from legacy unencrypted SQLite databases
 * to SQLCipher 256-bit AES encrypted databases.
 */
object SqlCipherMigrationHelper {

    enum class MigrationResult {
        FRESH_INSTALL,
        ALREADY_ENCRYPTED,
        MIGRATED_SUCCESSFULLY,
        SKIPPED_OR_ERROR
    }

    private val SQLITE_HEADER_PREFIX = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)

    /**
     * Checks if the target database file is plaintext SQLite. If so, exports it to an
     * encrypted SQLCipher database atomically.
     */
    @Synchronized
    fun migrateIfPlaintext(
        context: Context,
        databaseName: String = "fit10.db",
        passphrase: ByteArray
    ): MigrationResult {
        val dbFile = context.getDatabasePath(databaseName)
        if (!dbFile.exists() || dbFile.length() == 0L) {
            return MigrationResult.FRESH_INSTALL
        }

        if (!isPlaintextSqlite(dbFile)) {
            return MigrationResult.ALREADY_ENCRYPTED
        }

        val parentDir = dbFile.parentFile ?: return MigrationResult.SKIPPED_OR_ERROR
        val tempEncryptedFile = File(parentDir, "${databaseName}_encrypted_migrating")
        val backupFile = File(parentDir, "${databaseName}_unencrypted_backup")

        if (tempEncryptedFile.exists()) tempEncryptedFile.delete()
        if (backupFile.exists()) backupFile.delete()

        return try {
            val hexKey = passphrase.joinToString("") { "%02x".format(it) }

            // Open existing plaintext database with SQLCipher using blank/null passphrase
            val plaintextDb = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                "",
                null,
                SQLiteDatabase.OPEN_READWRITE,
                null
            )

            try {
                plaintextDb.execSQL("ATTACH DATABASE '${tempEncryptedFile.absolutePath}' AS encrypted KEY \"x'$hexKey'\";")
                plaintextDb.execSQL("SELECT sqlcipher_export('encrypted');")
                plaintextDb.execSQL("DETACH DATABASE encrypted;")
            } finally {
                plaintextDb.close()
            }

            // Verify the encrypted file was produced and is non-empty
            if (!tempEncryptedFile.exists() || tempEncryptedFile.length() == 0L) {
                tempEncryptedFile.delete()
                return MigrationResult.SKIPPED_OR_ERROR
            }

            // Atomic swap
            if (!dbFile.renameTo(backupFile)) {
                tempEncryptedFile.delete()
                return MigrationResult.SKIPPED_OR_ERROR
            }

            if (!tempEncryptedFile.renameTo(dbFile)) {
                // Rollback
                backupFile.renameTo(dbFile)
                return MigrationResult.SKIPPED_OR_ERROR
            }

            // Clean up auxiliary WAL/SHM files and the unencrypted backup
            File(parentDir, "$databaseName-wal").delete()
            File(parentDir, "$databaseName-shm").delete()
            backupFile.delete()

            MigrationResult.MIGRATED_SUCCESSFULLY
        } catch (e: Exception) {
            if (tempEncryptedFile.exists()) tempEncryptedFile.delete()
            if (backupFile.exists() && !dbFile.exists()) {
                backupFile.renameTo(dbFile)
            }
            MigrationResult.SKIPPED_OR_ERROR
        }
    }

    /**
     * Inspects the first 16 bytes of the file for the standard unencrypted SQLite header.
     */
    fun isPlaintextSqlite(file: File): Boolean {
        if (!file.exists() || file.length() < 16) return false
        val header = ByteArray(16)
        try {
            FileInputStream(file).use { input ->
                val read = input.read(header)
                if (read < 16) return false
            }
        } catch (_: Exception) {
            return false
        }
        return header.contentEquals(SQLITE_HEADER_PREFIX)
    }
}
