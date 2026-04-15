package com.el.mybasekotlin.helpers

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class JsonAssetController(private val context: Context) {

    fun readJsonFile(fileName: String): JSONObject? {
        return try {
            val inputStream = context.assets.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }

            bufferedReader.close()
            JSONObject(stringBuilder.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
