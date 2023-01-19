package com.example.sowoon.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.User

@Database(entities = [User::class, Profile::class, Gallery::class], version = 10)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase(): RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun galleryDao(): GalleryDao

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