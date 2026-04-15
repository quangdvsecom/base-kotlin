package com.el.mybasekotlin.helpers

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Locale

object FileHelper {
    private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    fun Context.createTempFileWithExtension(
        folder: String,
        extension: String,
        id: String? = null
    ): File? {
        return try {
            val fileDir = File(this.getExternalFilesDir(null), folder)
            if (!fileDir.exists()) {
                fileDir.mkdir()
            }
            val name = if (id == null) {
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis())
            } else {
                "${id}_${extension}_${
                    SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis())
                }"
            }
            fileDir.let { dir -> File.createTempFile(name, ".${extension}", dir) } ?: null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun Context.saveFileToFolder(file: File, folderName: String): File? {
        return try {
            val folder = File(this.getExternalFilesDir(null), folderName)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val destinationFile = File(folder, file.name)
            if (destinationFile.exists()) {
                destinationFile.delete()
            }
            file.copyTo(destinationFile, overwrite = true)
            destinationFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteAllFilesInDirectory(directoryPath: String) {
        try {
            val directory = File(directoryPath)
            if (directory.isDirectory) {
                val files = directory.listFiles()
                if (files != null) {
                    for (file in files) {
                        if (file.isFile) {
                            file.delete()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    fun getAllFileFromFolder(directoryPath: String): List<File> {
        val rs = arrayListOf<File>()
        try {
            val directory = File(directoryPath)
            if (directory.isDirectory) {
                val files = directory.listFiles()
                if (files != null) {
                    for (file in files) {
                        if (file.isFile) {
                            rs.add(file)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rs
    }
    /**
     * Quang edit
     */
    fun moveVideoToPublic(context: Context, privateFile: File): Uri? {
        val fileName = "GameRecord_${System.currentTimeMillis()}.mp4"
        val contentResolver = context.contentResolver

        // 1. Thiết lập thông tin file cho MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Đưa vào thư mục Movies/MyGameApp
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/MyGameApp")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }

        // 2. Chèn vào MediaStore để lấy Uri đích
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val publicUri = contentResolver.insert(collection, contentValues)

        publicUri?.let { uri ->
            try {
                // 3. Thực hiện copy dữ liệu từ file private sang public uri
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    FileInputStream(privateFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                // 4. Hoàn tất (bỏ chế độ pending để file hiển thị)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }

                Timber.d("QuangDv Move thành công! File ở: $uri")
                return uri
            } catch (e: Exception) {
                Timber.e(e, "QuangDv Lỗi khi move file")
                contentResolver.delete(uri, null, null)
            }
        }
        return null
    }
}