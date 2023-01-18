package com.example.sowoon.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sowoon.data.entity.User

@Database(entities = [User::class], version = 2)
abstract class AppDatabase(): RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if(instance == null){
                synchronized(AppDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "database"
                    ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
                }
            }
            return instance
        }
    }
}