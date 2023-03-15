package com.example.sowoon.database

import androidx.room.*
import com.example.sowoon.data.entity.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM UserTable WHERE email=:email  AND password = :password")
    fun getUser(email: String, password: String): User?

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM UserTable WHERE id=:userId")
    fun getUserProfile(userId: Int): User

    @Query("UPDATE UserTable SET ifArtist=:bool WHERE id=:id")
    fun ifArtistRegist(id: Int, bool: Boolean)

    @Query("SELECT ifArtist FROM UserTable WHERE id=:userId")
    fun ifArtist(userId: Int): Boolean

    @Query("UPDATE UserTable SET pushToken=:pushToken WHERE id=:userId")
    fun insertPushToken(userId: Int, pushToken: String)
}