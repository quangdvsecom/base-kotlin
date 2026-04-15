package com.el.mybasekotlin.data.local.database


import com.el.mybasekotlin.data.model.TestUser
import kotlinx.coroutines.flow.Flow

interface DatabaseHelper {
    fun getUsers(): Flow<List<TestUser>>

}