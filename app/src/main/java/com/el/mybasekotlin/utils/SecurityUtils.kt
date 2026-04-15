package com.el.mybasekotlin.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.el.mybasekotlin.BuildConfig
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


fun getOrCreateSecretKey(): SecretKey {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    val keyAlias = "demo_key_store_encryption"
    return if (keyStore.containsAlias(keyAlias)) {
        val secretKeyEntry = keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
        secretKeyEntry.secretKey
    } else {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        keyGenerator.generateKey()
    }
}


fun saveEncryptedApiKey(context: Context, encryptedKey: String) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("encrypted_api_key", encryptedKey).apply()
}


fun encryptData(data: String, secretKey: SecretKey): String {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val iv = cipher.iv  // IV (Initialization Vector) cần để giải mã sau này
    val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

    return Base64.encodeToString(iv + encryptedData, Base64.DEFAULT)
}

fun getDecryptedApiKey(context: Context): String? {
    val secretKey = getOrCreateSecretKey()

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    val encryptedApiKey = sharedPreferences.getString("encrypted_api_key", "")
    return encryptedApiKey?.let { decryptData(it, secretKey) }
}

fun decryptData(encryptedData: String, secretKey: SecretKey): String {
    val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
    val iv = decodedData.copyOfRange(0, 12)  // IV nằm ở đầu dữ liệu
    val encryptedBytes = decodedData.copyOfRange(12, decodedData.size)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

    return String(cipher.doFinal(encryptedBytes), Charsets.UTF_8)
}

fun decryptDataCustom(encryptedData: String, secretKey: SecretKey): String {
    // Kiểm tra Base64 decode
    val decodedData = try {
        Base64.decode(encryptedData, Base64.DEFAULT)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Dữ liệu mã hóa không hợp lệ!", e)
    }

    // Kiểm tra kích thước tối thiểu (IV + dữ liệu)
    if (decodedData.size < 12) throw IllegalArgumentException("Dữ liệu không đủ dài để chứa IV")

    val iv = decodedData.copyOfRange(0, 12)  // IV nằm ở đầu dữ liệu
    val encryptedBytes = decodedData.copyOfRange(12, decodedData.size)

    // Kiểm tra dữ liệu mã hóa
    if (encryptedBytes.isEmpty()) throw IllegalArgumentException("Dữ liệu mã hóa không hợp lệ")

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

    return try {
        String(cipher.doFinal(encryptedBytes), Charsets.UTF_8)
    } catch (e: Exception) {
        throw IllegalArgumentException("Giải mã thất bại! Dữ liệu không hợp lệ hoặc sai khóa.", e)
    }
}