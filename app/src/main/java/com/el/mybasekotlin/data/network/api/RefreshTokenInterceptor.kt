package com.el.mybasekotlin.data.network.api

import com.el.mybasekotlin.data.local.AppPreferences
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ElChuanmen on 3/17/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
class RefreshTokenInterceptor(
    private val refreshTokenService: RefreshTokenService
) : Interceptor {
    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Nếu token hết hạn (401)
        if (response.code == 401) {
            response.close() // Đóng response cũ

            // Gửi request refresh token
            val newTokenResponse = refreshTokenService.refreshToken(AppPreferences.accessToken).execute()

            if (newTokenResponse.isSuccessful) {
                val newToken = newTokenResponse.body()?.accessToken
                if (newToken != null) {
                    AppPreferences.accessToken=newToken
                }

                // Gửi lại request ban đầu với token mới
                val newRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer $newToken")
                    .build()
                return chain.proceed(newRequest)
            }
        }
        return response
    }
}
