package com.example.sowoon.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.sowoon.data.entity.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM UserTable WHERE email=:email  AND password = :password")
    fun getUser(email: String, password: String): User?

    @Delete
    fun deleteUser(user: User)

    @Query("UPDATE UserTable SET ifArtist=:bool WHERE id=:id")
    fun ifArtistRegist(id: Int, bool: Boolean)

    @Query("UPDATE UserTable SET likeGallery=:likeGallery WHERE id=:id")
    fun addLikeGallery(id: Int, likeGallery: ArrayList<Int>?)

    @Query("SELECT likeGallery FROM UserTable WHERE id=:id")
    fun getLikeGallery(id: Int): ArrayList<Int>?

}