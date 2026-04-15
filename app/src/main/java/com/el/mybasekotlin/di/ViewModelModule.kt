package com.el.mybasekotlin.di

import com.el.mybasekotlin.data.local.database.DatabaseHelper
import com.el.mybasekotlin.data.local.database.DatabaseHelperImpl
import com.el.mybasekotlin.data.network.api.ApiHelper
import com.el.mybasekotlin.data.network.api.ApiHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by ElChuanmen on 1/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
interface ViewModelModule {
    @Binds
    fun provideApiHelper(apiHelperImpl: ApiHelperImpl): ApiHelper

    @Binds
    fun provideDbHelper(dbHelper: DatabaseHelperImpl): DatabaseHelper
}