package com.el.mybasekotlin.data.network.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.el.mybasekotlin.BuildConfig
import com.el.mybasekotlin.data.local.AppPreferences
import com.el.mybasekotlin.data.network.interceptor.ErrorInterceptor
import com.el.mybasekotlin.data.network.interceptor.NetworkCheckerInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException

/**
 * Created by ElChuanmen on 1/15/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
class RetrofitClient {

    companion object {
        val TYPE_DOMAIN_API_DEFAULT = 1
        val TYPE_DOMAIN_API_CONFIG = 2
        fun createRetrofitInstance(
            context: Context,
            typeDomain: Int,
            refreshTokenService: RefreshTokenService?
        ): Retrofit {
            val baseUrl = when (typeDomain) {
                TYPE_DOMAIN_API_DEFAULT -> {
                    BuildConfig.API_DOMAIN
                }

                TYPE_DOMAIN_API_CONFIG -> {
                    "https://mobile.ewings.vn/"
                }

                else -> {
                    "        BuildConfig.API_DOMAIN"
                }
            }

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            //HttpHeaderInterceptor
            val httpHeaderInterceptor = HttpHeaderInterceptor()
            //NetWork
            val networkCheckerInterceptor = NetworkCheckerInterceptor(context)

            val chuckerInterceptor = ChuckerInterceptor.Builder(context).collector(
                ChuckerCollector(
                    context = context,
                    showNotification = true,
                    retentionPeriod = RetentionManager.Period.ONE_HOUR
                )
            ).maxContentLength(250_000L).redactHeaders("Auth-Token", "Bearer")
                .alwaysReadResponseBody(true).build()

            //ErrorINterceptor
            val errorInterceptor = ErrorInterceptor(context)
            //-------------------------
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(networkCheckerInterceptor)
            if (BuildConfig.DEBUG) httpClient.addInterceptor(logging)
            httpClient.addInterceptor(httpHeaderInterceptor)
            httpClient.addInterceptor(errorInterceptor)
            httpClient.addInterceptor(chuckerInterceptor)
            if (refreshTokenService != null)
                httpClient.addInterceptor(RefreshTokenInterceptor(refreshTokenService))

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build())
                .build()
        }

        internal class HttpHeaderInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original: Request = chain.request()
                Timber.d("Api Client Token : ${AppPreferences.accessToken}")
                val request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .apply {
                        if (AppPreferences.accessToken.isNotEmpty()) {
                            addHeader("Authorization", "Bearer " + AppPreferences.accessToken)
                        }
                    }
                    .method(original.method, original.body)
                    .build()

                return chain.proceed(request)
            }
        }
    }
}