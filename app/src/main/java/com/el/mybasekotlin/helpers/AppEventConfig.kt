package com.el.mybasekotlin.helpers

/**
 * Created by ElChuanmen on 1/15/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
const val EVENT_LOGIN_SUCCESS = 1
const val EVENT_REGISTER_SUCCESS = 2
const val EVENT_LOGOUT = 3
const val EVENT_NEW_SEARCH = 4

data class AuthEvent(val event: Int)

data class UpdateInfo(val isSuccess: Boolean)

const val EVENT_ACCESS = 1
const val EVENT_DEFENDED = 2

data class PermissionChange(val event: Int)
data class TestMessage(val msg: String)


object RefreshTokenFCM {

}
data class SearchEvent(val event: Int,val keySearch:String)