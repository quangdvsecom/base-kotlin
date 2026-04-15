package com.el.mybasekotlin.utils.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Base64
import android.widget.Toast
import java.security.SecureRandom

fun String.toSlug() = lowercase()
    .replace("\n", " ")
    .replace("[^a-z\\d\\s]".toRegex(), " ")
    .split(" ")
    .joinToString("-")
    .replace("-+".toRegex(), "-")

fun generateEncryptionKey(): ByteArray {
    val secureRandom = SecureRandom()
    val encryptionKey = ByteArray(16)
    secureRandom.nextBytes(encryptionKey)
    return encryptionKey
}

fun String?.getOrBlank(): String {
    return this ?: ""
}

fun bytesToHex(bytes: ByteArray): String? {
    val result = StringBuilder()
    for (b in bytes) {
        result.append(String.format("%02x", b))
    }
    return result.toString()
}

fun convertByteToString(array: ByteArray): String? {
    val saveThis: String = Base64.encodeToString(array, Base64.DEFAULT)
    return saveThis
}

fun convertStringToByte(stringFromSharedPrefs: String): ByteArray {
    val array = Base64.decode(stringFromSharedPrefs, Base64.DEFAULT)
    return array
}

fun String.copyToClipboard(context: Context) {
    val clipboard: ClipboardManager? =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip: ClipData = ClipData.newPlainText("Copied Text", this)
    clipboard?.setPrimaryClip(clip)

    Toast.makeText(context, "Đã sao chép mã thiết bị", Toast.LENGTH_LONG).show()
}

fun String.removeAllSpace(): String? {
//   return this.replace("\\s+".toRegex(), "")
    return this.split("\\s+".toRegex()).joinToString("")
}

fun String.removeSpecialCharacters(): String {
//    return this.replace(Regex("[n|x|*|k|d]"), "") // có thể bỏ nếu cần
    return this.replace(Regex("[x|*]"), "")
}

fun String.removeMatchingSubstring(regexPattern: Regex): String {
    return this.replace(regexPattern, "")
}

fun extractSubstring(input: String): String {
    val spaceIndex = input.indexOf(' ')
    val commaIndex = input.indexOf(',')

    // Determine the first delimiter index
    val delimiterIndex = when {
        spaceIndex != -1 && commaIndex != -1 -> minOf(spaceIndex, commaIndex)
        spaceIndex != -1 -> spaceIndex
        commaIndex != -1 -> commaIndex
        else -> -1
    }

    return if (delimiterIndex != -1) {
        input.substring(0, delimiterIndex)
    } else {
        input // If no delimiter found, return the whole input
    }
}

fun extractAndRemoveSubstring(input: String): ExtractionResult {
    val spaceIndex = input.indexOf(' ')
    val commaIndex = input.indexOf(',')

    // Determine the first delimiter index
    val delimiterIndex = when {
        spaceIndex != -1 && commaIndex != -1 -> minOf(spaceIndex, commaIndex)
        spaceIndex != -1 -> spaceIndex
        commaIndex != -1 -> commaIndex
        else -> -1
    }

    if (delimiterIndex != -1) {
        val extracted = input.substring(0, delimiterIndex)
        val modifiedInput = input.replaceFirst(extracted, "").trimStart()
        return ExtractionResult(extracted, modifiedInput)
    } else {
        return ExtractionResult(input, "")
    }
}

data class ExtractionResult(val extracted: String, val modifiedInput: String)

//Last character
fun lastChar(input: String): String {
    return input.last().toString()
}

fun getLastWordWithoutPunctuation(input: String): String {
    val words = input.trim().split("\\s+".toRegex())
    if (words.isNotEmpty()) {
        val lastWord = words.last()
        return lastWord.removeSuffix(".").removeSuffix(",")
    }
    return ""
}

fun processStringX(input: String): String? {
    return input.replace("(\\S)x".toRegex(), "$1 x").replace("x\\s+".toRegex(), "x")
}

fun isStringAnInt(str: String): Boolean {
    return str.toIntOrNull() != null
}