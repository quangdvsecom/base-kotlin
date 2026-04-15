package com.el.mybasekotlin.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.el.mybasekotlin.data.model.TestUser

@Database(entities = [TestUser::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testUserDao(): TestUserDao
}