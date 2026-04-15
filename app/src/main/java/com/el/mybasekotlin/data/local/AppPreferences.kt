package com.el.mybasekotlin.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by ElChuanmen on 7/16/2024.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
object AppPreferences {
    const val NAME = "MY_BASE_KOTLIN"
    const val MODE = Context.MODE_PRIVATE
    lateinit var preferences: SharedPreferences

    val gson = Gson()

    // list of app specific preferences
    private val NIGHT_MODE_PREF = Pair("NIGHT_MODE_PREF", AppCompatDelegate.MODE_NIGHT_YES)
    private val LOCALE_STRING_PREF = Pair("LOCALE_STRING_PREF", "en")
    private val ACCESS_TOKEN_PREF = Pair("ACCESS_TOKEN_PREF", "")
    private val MAP_TOKEN_PREF = Pair("MAP_TOKEN_PREF", "")
    private val EXPIRES_IN_PREF = Pair("EXPIRES_IN_PREF", 0L)
    private val FCM_TOKEN_PREF = Pair("FCM_TOKEN__IN_PREF", "")
    private val IS_LOGIN = Pair("IS_LOGIN", false)
    private val USER_PREF = Pair("USER_PREF", "")
    private val AVATAR_PREF = Pair("AVATAR_PREF", "")
    private val USER_ID_CODE_PREF = Pair("USER_ID_CODE_PREF", "")
    private val USER_EMAIL_PREF = Pair("USER_EMAIL_PREF", "")
    private val EVENT_ID_CODE_PREF = Pair("EVENT_ID_CODE_PREF", "")
    private val SCHEDULE_ID_EVENT = Pair("SCHEDULE_ID_EVENT", "")
    private val PROJECT_ID_EVENT = Pair("PROJECT_ID_EVENT", "")
    private val NOTICE_NO_READ = Pair("NOTICE_NO_READ", "0")
    private val FIRST_OPEN_APP = Pair("FIRST_OPEN_APP", true)
    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    var isLogIn: Boolean
        get() = get<Boolean>(IS_LOGIN.first) ?: false
        set(value) = save(IS_LOGIN.first, value)

    var accessToken: String
        get() = get<String>(ACCESS_TOKEN_PREF.first) ?: ACCESS_TOKEN_PREF.second
        set(value) = save(ACCESS_TOKEN_PREF.first, value)

    var mapToken: String
        get() = get<String>(MAP_TOKEN_PREF.first) ?: MAP_TOKEN_PREF.second
        set(value) = save(MAP_TOKEN_PREF.first, value)

    var fcmToken: String
        get() = get<String>(FCM_TOKEN_PREF.first) ?: FCM_TOKEN_PREF.second
        set(value) = save(FCM_TOKEN_PREF.first, value)

//    var user: User?
//        get() = get<User>(USER_PREF.first)
//        set(value) = save(USER_PREF.first, value)

    var userIdCode: String
        get() = get<String>(USER_ID_CODE_PREF.first) ?: USER_ID_CODE_PREF.second
        set(value) = save(USER_ID_CODE_PREF.first, value)

    var userEmail: String
        get() = get<String>(USER_EMAIL_PREF.first) ?: USER_EMAIL_PREF.second
        set(value) = save(USER_EMAIL_PREF.first, value)

    var scheduleId: String
        get() = get<String>(SCHEDULE_ID_EVENT.first) ?: SCHEDULE_ID_EVENT.second
        set(value) = save(SCHEDULE_ID_EVENT.first, value)

    var projectId: String
        get() = get<String>(PROJECT_ID_EVENT.first) ?: PROJECT_ID_EVENT.second
        set(value) = save(PROJECT_ID_EVENT.first, value)

    var avatar: String
        get() = get<String>(AVATAR_PREF.first) ?: AVATAR_PREF.second
        set(value) = save(AVATAR_PREF.first, value)

    var eventId: String
        get() = get<String>(EVENT_ID_CODE_PREF.first) ?: EVENT_ID_CODE_PREF.second
        set(value) = save(EVENT_ID_CODE_PREF.first, value)
    var isFirstOpenApp: Boolean
        get() = get<Boolean>(FIRST_OPEN_APP.first) ?: FIRST_OPEN_APP.second
        set(value) = save(FIRST_OPEN_APP.first, value)
    var noticeNotRead: String
        get() = get<String>(NOTICE_NO_READ.first) ?: NOTICE_NO_READ.second
        set(value) = save(NOTICE_NO_READ.first, value)

    fun clearData() {
        val editor = preferences.edit()
        editor?.clear()
        editor?.apply()
    }

    inline fun <reified T> save(key: String, any: T) {
        val editor = preferences.edit()
        when (any) {
            is String -> editor?.putString(key, any)
            is Float -> editor?.putFloat(key, any)
            is Int -> editor?.putInt(key, any)
            is Long -> editor?.putLong(key, any)
            is Boolean -> editor?.putBoolean(key, any)
            else -> editor?.putString(key, gson.toJson(any))
        }
        editor?.apply()
    }

    inline fun <reified T> get(key: String): T? {
        when (T::class) {
            Float::class -> return preferences.getFloat(key, 0f) as T
            Int::class -> return preferences.getInt(key, 0) as T
            Long::class -> return preferences.getLong(key, 0) as T
            String::class -> return preferences.getString(key, "") as T
            Boolean::class -> return preferences.getBoolean(key, false) as T
            else -> {
                val any = preferences.getString(key, "")
                val type = object : TypeToken<T>() {}.type
                if (!any.isNullOrEmpty()) {
                    return gson.fromJson<T>(any, type)
                }
            }
        }
        return null
    }

//    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
//        val editor = edit()
//        operation(editor)
//        editor.apply()
//    }
}