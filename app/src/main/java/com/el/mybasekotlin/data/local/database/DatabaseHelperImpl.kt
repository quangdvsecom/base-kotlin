package com.el.mybasekotlin.data.local.database

import com.el.mybasekotlin.data.local.database.AppDatabase
import com.el.mybasekotlin.data.local.database.DatabaseHelper
import com.el.mybasekotlin.data.model.TestUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DatabaseHelperImpl @Inject constructor(private val appDatabase: AppDatabase) :
    DatabaseHelper {
    override fun getUsers(): Flow<List<TestUser>> = flow { emit(appDatabase.testUserDao().getAll()) }
}