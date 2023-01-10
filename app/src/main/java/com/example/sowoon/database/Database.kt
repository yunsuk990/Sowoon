package com.example.sowoon.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sowoon.data.entity.User

@Database(entities = [User::class], version = 1)
abstract class Database(): RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private var instance: com.example.sowoon.database.Database? = null

        @Synchronized
        fun getInstance(context: Context): com.example.sowoon.database.Database? {
            if(instance == null){
                synchronized(com.example.sowoon.database.Database::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        com.example.sowoon.database.Database::class.java,
                        "database"
                    ).allowMainThreadQueries().build()
                }
            }
            return instance
        }
    }

}