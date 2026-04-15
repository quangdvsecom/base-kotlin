package com.el.mybasekotlin.data.network.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * Created by ElChuanmen on 2/7/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
class DownloadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_FILE_URL = "file_url"
        const val KEY_FILE_NAME = "file_name"
    }

    override suspend fun doWork(): Result {
        val fileUrl = inputData.getString(KEY_FILE_URL)
        val fileName = inputData.getString(KEY_FILE_NAME)

        return try {
            downloadFile(fileUrl, fileName)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun downloadFile(fileUrl: String?, fileName: String?) {
        withContext(Dispatchers.IO) {
            val url = URL(fileUrl)
            val connection = url.openConnection()
            connection.connect()

            val inputStream = connection.getInputStream()
            val file = File(applicationContext.filesDir, fileName) // Lưu vào thư mục files của ứng dụng
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.close()
            inputStream.close()
        }
    }
}