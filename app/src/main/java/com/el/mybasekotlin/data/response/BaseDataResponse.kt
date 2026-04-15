package com.el.mybasekotlin.data.response

import com.el.mybasekotlin.data.state.ResponseCode
import com.google.gson.annotations.SerializedName

class BaseDataResponse<T>(
    @SerializedName("message") var message: String = "",
    @SerializedName(value = "error_code", alternate = ["err_code"]) var errorCode: String? = "",
    @SerializedName("status") val status: Int,
    @SerializedName("code") val code: String?="",
    @SerializedName("data") val data: T? = null
) {
    val isSuccess: Boolean
        get() = status == ResponseCode.SERVER_SUCCESS.code && errorCode == "0"

    val isFailed: Boolean
        get() = errorCode != ResponseCode.SERVER_SUCCESS.code.toString()

    val isDataNotNull: Boolean
        get() = data != null
}
class BaseResponseNotice<T>(
    @SerializedName("unread") var unread: Int = 0,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("list") val list: T? = null
)
class BaseDataNoticeResponse<T>(
    @SerializedName("current_page") var current_page: Int = 0,
    @SerializedName("per_page") var per_page: Int = 0,
    @SerializedName("total") val status: Int = 0,
    @SerializedName("data") val data: T? = null
)