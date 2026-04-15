package com.el.mybasekotlin.data.network.api

import com.el.mybasekotlin.data.model.Notice
import com.el.mybasekotlin.data.model.Setting
import com.el.mybasekotlin.data.response.BaseDataNoticeResponse
import com.el.mybasekotlin.data.response.BaseDataResponse
import com.el.mybasekotlin.data.response.BaseResponseNotice
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by ElChuanmen on 1/16/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
interface RefreshTokenService {

    @POST("auth/refresh")
    fun refreshToken(@Body refreshToken: String): Call<RefreshTokenResponse>
}
data class RefreshTokenResponse(
    @SerializedName("access_token") val accessToken: String
)