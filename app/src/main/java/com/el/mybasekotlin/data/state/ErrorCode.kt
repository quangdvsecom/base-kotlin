package com.el.mybasekotlin.data.state

/**
 * Created by ElChuanmen on 1/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
enum class ErrorCode(val code: String) {
    NO_INTERNET("101"),
    SIGN_IN_ERROR("102"),
    WRONG_OTP("103"),
    SIGN_IN_ANOTHER_DEVICE("104"),
    TOKEN_EXPIRED("401"),
    AUTHENTICATION_FAILED("123s")
}
enum class  ResponseCode(val code : Int) {
    SERVER_ERROR(0),
    SERVER_SUCCESS(1)
}

object ErrorAction {
    const val ACTION_FORCE_LOGOUT = "com.action.ACTION_FORCE_LOGOUT"
}