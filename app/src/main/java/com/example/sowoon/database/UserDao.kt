package com.example.sowoon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sowoon.data.entity.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM UserTable WHERE email=:email  AND password = :password")
    fun getUser(email: String, password: String): User?

}