package com.el.mybasekotlin.utils.extension

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import timber.log.Timber

fun Activity.getScreenDensity() {
    val displayMetrics = resources.displayMetrics
    val density = displayMetrics.density
    val densityDpi = displayMetrics.densityDpi
    Timber.d("Density $density")
    Timber.d("Density dpi = $densityDpi")
    val densityType = when {
        (densityDpi <= DisplayMetrics.DENSITY_LOW) -> "ldpi"

        (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) -> "mdpi"

        (densityDpi <= DisplayMetrics.DENSITY_HIGH) -> "hdpi"

        (densityDpi <= DisplayMetrics.DENSITY_XHIGH) -> "xhdpi"

        (densityDpi <= DisplayMetrics.DENSITY_XXHIGH) -> "xxhdpi"

        (densityDpi <= DisplayMetrics.DENSITY_XXXHIGH) -> "xxxhdpi"

        else -> "Unknown Density"
    }
    val message = "Screen Density Type: $densityType"
    // Print to Logcat
    Timber.d("Density $message")
    val smallestWidthDp = resources.configuration.smallestScreenWidthDp
//    if (BuildConfig.DEBUG) {
//        Toast.makeText(
//            baseContext,
//            "DisplayInfo Smallest width in dp: $smallestWidthDp",
//            Toast.LENGTH_SHORT
//        ).show()
//    }
    Timber.d("DisplayInfo: smallest width in dp: $smallestWidthDp")

}
fun getScreenSize(context: Context): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
    windowManager?.defaultDisplay?.getMetrics(displayMetrics)

    val screenWidth = displayMetrics.widthPixels

    val dpInPixels = (60 * displayMetrics.density).toInt()
    val screenHeight = displayMetrics.heightPixels - dpInPixels

    return Pair(screenWidth, screenHeight)
}