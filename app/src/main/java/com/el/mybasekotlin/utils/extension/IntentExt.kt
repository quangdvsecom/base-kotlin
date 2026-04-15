package com.el.mybasekotlin.utils.extension


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import com.el.mybasekotlin.helpers.NOTIFICATION_SETTING
import com.google.gson.Gson
import java.io.Serializable

/**
 * Created by ElChuanmen on 1/14/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */

/**
 * Push value to activity
 * exam: openActivity<NoticeActivity>("key0" to "value0", "key1" to "value1")
 *
 */

inline fun <reified T : Activity> Context.openActivity(vararg params: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(*params)
    this.startActivity(intent)
}

fun Intent.putExtras(vararg params: Pair<String, Any>): Intent {
    if (params.isEmpty()) return this
    params.forEach { (key, value) ->
        when (value) {
            is Int -> putExtra(key, value)
            is Byte -> putExtra(key, value)
            is Char -> putExtra(key, value)
            is Long -> putExtra(key, value)
            is Float -> putExtra(key, value)
            is Short -> putExtra(key, value)
            is Double -> putExtra(key, value)
            is Boolean -> putExtra(key, value)
            is Bundle -> putExtra(key, value)
            is String -> putExtra(key, value)
            is IntArray -> putExtra(key, value)
            is ByteArray -> putExtra(key, value)
            is CharArray -> putExtra(key, value)
            is LongArray -> putExtra(key, value)
            is FloatArray -> putExtra(key, value)
            is Parcelable -> putExtra(key, value)
            is ShortArray -> putExtra(key, value)
            is DoubleArray -> putExtra(key, value)
            is BooleanArray -> putExtra(key, value)
            is CharSequence -> putExtra(key, value)
            is Array<*> -> {
                when {
                    value.isArrayOf<String>() -> putExtra(key, value as Array<String?>)
                    value.isArrayOf<Parcelable>() -> putExtra(key, value as Array<Parcelable?>)
                    value.isArrayOf<CharSequence>() -> putExtra(key, value as Array<CharSequence?>)
                    else -> putExtra(key, value)
                }
            }
            is Serializable -> putExtra(key, value)
        }
    }
    return this
}

//*************************************************
operator fun <T> Intent.set(key: String, value: T) {
    when (value) {
        is String -> this.putExtra(key, value)
        is Boolean -> this.putExtra(key, value)
        is Int -> this.putExtra(key, value)
        is Short -> this.putExtra(key, value)
        is Long -> this.putExtra(key, value)
        is Byte -> this.putExtra(key, value)
        is ByteArray -> this.putExtra(key, value)
        is Char -> this.putExtra(key, value)
        is CharArray -> this.putExtra(key, value)
        is CharSequence -> this.putExtra(key, value)
        is Float -> this.putExtra(key, value)
        is Parcelable -> this.putExtra(key, value)
        is Serializable -> this.putExtra(key, value)
        else -> throw IllegalStateException("Type of property $key is not supported")
    }
}

inline operator fun <reified T : Any> Intent.get(key: String, defaultValue: T? = null): T? {
    return when (T::class) {
        String::class -> getStringExtra(key) as T?
        Int::class -> getIntExtra(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBooleanExtra(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloatExtra(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLongExtra(key, defaultValue as? Long ?: -1) as T?
        else -> Gson().fromJson(getStringExtra(key), T::class.java)
    }
}


/**
 * id = define on the BaseConstant
 */
fun Context.getSetting(id: Int) = when (id) {
    NOTIFICATION_SETTING -> Settings.ACTION_APP_NOTIFICATION_SETTINGS
    else -> ""
}

//*************************************************
inline operator fun <reified T : Any> Bundle.get(key: String, defaultValue: T? = null): T? {
    return when (T::class) {
        String::class -> getString(key) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> Gson().fromJson(getString(key), T::class.java)
    }
}
inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}

fun Bundle.putExtrasBundle(vararg params: Pair<String, Any>): Bundle {
    if (params.isEmpty()) return this
    params.forEach { (key, value) ->
        when (value) {
            is Int -> putInt(key, value)
            is Byte -> putByte(key, value)
            is Char -> putChar(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Short -> putShort(key, value)
            is Double -> putDouble(key, value)
            is Boolean -> putBoolean(key, value)
            is Bundle -> putBundle(key, value)
            is String -> putString(key, value)
            is IntArray -> putIntArray(key, value)
            is ByteArray -> putByteArray(key, value)
            is CharArray -> putCharArray(key, value)
            is LongArray -> putLongArray(key, value)
            is FloatArray -> putFloatArray(key, value)
            is Parcelable -> putParcelable(key, value)
            is ShortArray -> putShortArray(key, value)
            is DoubleArray -> putDoubleArray(key, value)
            is BooleanArray -> putBooleanArray(key, value)
            is CharSequence -> putCharSequence(key, value)
            is Array<*> -> {
                when {
                    value.isArrayOf<String>() -> putStringArray(key, value as Array<String?>)
                    value.isArrayOf<Parcelable>() -> putParcelableArray(
                        key, value as Array<Parcelable?>
                    )
                    value.isArrayOf<CharSequence>() -> putCharSequenceArray(
                        key, value as Array<CharSequence?>
                    )

                }
            }
            is Serializable -> putSerializable(key, value)
        }
    }
    return this
}





