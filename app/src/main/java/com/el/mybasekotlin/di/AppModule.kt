package com.el.mybasekotlin.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.el.mybasekotlin.data.local.AppPreferences
import com.el.mybasekotlin.data.local.database.AppDatabase
import com.el.mybasekotlin.data.local.database.DatabaseBuilder
import com.el.mybasekotlin.data.network.api.ApiConfigService
import com.el.mybasekotlin.data.network.api.ApiService
import com.el.mybasekotlin.data.network.api.OtherApiService
import com.el.mybasekotlin.data.network.api.RefreshTokenInterceptor
import com.el.mybasekotlin.data.network.api.RefreshTokenService
import com.el.mybasekotlin.data.network.api.RetrofitClient
import com.el.mybasekotlin.helpers.JsonAssetController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

/**
 * Created by ElChuanmen on 1/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): MyApplication {
        return app as MyApplication
    }

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().setStrictness(Strictness.LENIENT).create()
    }
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context): AppDatabase =
        DatabaseBuilder.getInstance(app)
    @Singleton
    @Provides
    fun sharedPreference(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(AppPreferences.NAME, AppPreferences.MODE)
    @Provides
    fun provideSingleExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }
    @Provides
    @Singleton
    fun provideApiServiceDefault(@ApplicationContext app: Context ,
                                 refreshTokenService: RefreshTokenService): ApiService
            = RetrofitClient.createRetrofitInstance(app, RetrofitClient.TYPE_DOMAIN_API_DEFAULT,refreshTokenService).create(
        ApiService::class.java)
    @Provides
    @Singleton
    fun provideApiServiceOther(@ApplicationContext app: Context, refreshTokenService: RefreshTokenService): OtherApiService
            = RetrofitClient.createRetrofitInstance(app, RetrofitClient.TYPE_DOMAIN_API_CONFIG,refreshTokenService).create(OtherApiService::class.java)

    @Provides
    @Singleton
    fun provideApiServiceConfig(@ApplicationContext app: Context,
                                refreshTokenService: RefreshTokenService): ApiConfigService
            = RetrofitClient.createRetrofitInstance(app, RetrofitClient.TYPE_DOMAIN_API_CONFIG,refreshTokenService).create(
        ApiConfigService::class.java)

    @Provides
    @Singleton
    fun provideApiRefreshToken(@ApplicationContext app: Context): RefreshTokenService
            = RetrofitClient.createRetrofitInstance(app, RetrofitClient.TYPE_DOMAIN_API_DEFAULT,null).create(
        RefreshTokenService::class.java)
    @Singleton
    @Provides
    fun provideJsonAssetController(@ApplicationContext app: Context): JsonAssetController {
        return JsonAssetController(app)
    }

//    @Provides
//    @Singleton
//    fun provideRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("https://your-api.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideRefreshTokenService(retrofit: Retrofit): RefreshTokenService {
//        return retrofit.create(RefreshTokenService::class.java)
//    }

}