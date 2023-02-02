package com.example.sowoon.database

import androidx.room.*
import com.example.sowoon.data.entity.User

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
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
    fun addLikeGallery(id: Int, likeGallery: ArrayList<String>?)


}