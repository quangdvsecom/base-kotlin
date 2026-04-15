package com.el.mybasekotlin.data.network.api

import android.content.SharedPreferences
import com.el.mybasekotlin.data.local.AppPreferences

/**
 * Created by ElChuanmen on 3/17/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
class TokenManager() {

    fun saveAccessToken(token: String) {
        AppPreferences.accessToken=token
    }

    fun getAccessToken(): String {
        return AppPreferences.accessToken ?: ""
    }

//    fun saveRefreshToken(token: String) {
//        sharedPreferences.edit().putString("REFRESH_TOKEN", token).apply()
//    }
//
//    fun getRefreshToken(): String {
//        return sharedPreferences.getString("REFRESH_TOKEN", "") ?: ""
//    }
}
