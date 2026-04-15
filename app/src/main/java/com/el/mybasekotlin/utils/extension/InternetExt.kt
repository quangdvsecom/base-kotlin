package vn.chayluoi.stream.app.utils.extension

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by ElChuanmen on 11/21/2022.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
fun hasConnection(context: Context?): Boolean {
    if (context == null) {
        return false
    }
    val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    if (wifiNetwork != null && wifiNetwork.isConnected) {
        return true
    }
    val mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    if (mobileNetwork != null && mobileNetwork.isConnected) {
        return true
    }
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}