package com.el.mybasekotlin.utils.extension

import android.content.Context
import java.io.IOException
import java.io.InputStream

fun loadJSONFromAsset(fileName: String?, context: Context): String? {
    return try {
        val open: InputStream = context.assets.open(fileName!!)
        val bArr = ByteArray(open.available())
        open.read(bArr)
        open.close()
        String(bArr, charset("UTF-8"))
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}