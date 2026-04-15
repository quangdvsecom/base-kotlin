package com.vcc.ticket.utils.extension

import android.net.Uri
import java.util.regex.Pattern

fun String.isYouTubeUrl(): Boolean {
    val pattern = Pattern.compile(
        "^(https?://)?(www\\.)?(youtube|youtu|youtube-nocookie)\\.(com|be)/.+\$",
        Pattern.CASE_INSENSITIVE
    )
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.isVccUrl(): Boolean {
    val pattern =
        Pattern.compile("^(https?://)?(www\\.)?(cms-event\\.dev\\.admicro\\.vn|hybrid-event\\.vn)(/.*)?$")
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.addParam(params : Map<String,String>) : String {
    return Uri.Builder()
        .encodedPath(this)
        .apply {
            params.forEach { (key, value) ->
                appendQueryParameter(key, value)
            }
        }
        .build()
        .toString()
}