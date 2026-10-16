package com.fit10.app.data.db

import com.fit10.app.core.security.DatabaseKeyManager
import com.fit10.app.data.db.migration.SqlCipherMigrationHelper
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class SqlCipherDatabaseTest {

    @Test
    fun isPlaintextSqlite_identifiesStandardHeader() {
        val tempFile = File.createTempFile("test_sqlite", ".db")
        try {
            FileOutputStream(tempFile).use { out ->
                out.write("SQLite format 3\u0000".toByteArray(Charsets.US_ASCII))
                out.write(ByteArray(100) { 0 })
            }
            assertTrue("Should detect standard unencrypted SQLite header", SqlCipherMigrationHelper.isPlaintextSqlite(tempFile))
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun isPlaintextSqlite_identifiesEncryptedOrRandomFile() {
        val tempFile = File.createTempFile("test_encrypted", ".db")
        try {
            FileOutputStream(tempFile).use { out ->
                val randomBytes = ByteArray(116)
                SecureRandom().nextBytes(randomBytes)
                out.write(randomBytes)
            }
            assertFalse("Encrypted file without magic header must not be identified as plaintext", SqlCipherMigrationHelper.isPlaintextSqlite(tempFile))
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun aesGcm_encryptDecryptRoundTrip() {
        val rawPassphrase = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val masterKeyBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val masterKey = SecretKeySpec(masterKeyBytes, "AES")

        val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, masterKey)
        val iv = encryptCipher.iv
        val ciphertext = encryptCipher.doFinal(rawPassphrase)

        assertNotNull(iv)
        assertEquals(12, iv.size)
        assertTrue(ciphertext.size >= rawPassphrase.size + 16) // includes 128-bit GCM tag

        // Decrypt with correct key
        val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        decryptCipher.init(Cipher.DECRYPT_MODE, masterKey, GCMParameterSpec(128, iv))
        val decrypted = decryptCipher.doFinal(ciphertext)

        assertArrayEquals("Decrypted passphrase must match original", rawPassphrase, decrypted)
    }

    @Test(expected = AEADBadTagException::class)
    fun aesGcm_wrongKeyRejectionThrows() {
        val rawPassphrase = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val masterKey = SecretKeySpec(ByteArray(32) { 1 }, "AES")
        val wrongKey = SecretKeySpec(ByteArray(32) { 2 }, "AES")

        val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, masterKey)
        val iv = encryptCipher.iv
        val ciphertext = encryptCipher.doFinal(rawPassphrase)

        // Attempt decrypt with wrong key
        val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        decryptCipher.init(Cipher.DECRYPT_MODE, wrongKey, GCMParameterSpec(128, iv))
        decryptCipher.doFinal(ciphertext)
    }

    @Test
    fun databaseKeyManager_testPassphraseLifecycle() {
        val testKey = ByteArray(32) { 0x07 }
        DatabaseKeyManager.setTestPassphrase(testKey)

        val retrieved = DatabaseKeyManager.getOrCreateDatabasePassphrase(
            // pass dummy or mock context; will use test fallback
            android.content.ContextWrapper(null)
        )
        assertArrayEquals(testKey, retrieved)

        DatabaseKeyManager.setTestPassphrase(null)
    }
}
