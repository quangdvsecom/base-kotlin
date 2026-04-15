package com.el.mybasekotlin.data.state

/**
 * Created by ElChuanmen on 1/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
sealed class DataState<out T> {
    data class Success<out T>(val data: T?) : DataState<T>()
    data class Error(
        val code: String? = null,
        val message: String,
        val isException: Boolean = false
    ) : DataState<Nothing>()

    data object Loading : DataState<Nothing>()
    data object Empty : DataState<Nothing>()
}
fun <T> DataState<T>.getSuccessDataOrNull(): T? {
    return if (this is DataState.Success) this.data else null
}