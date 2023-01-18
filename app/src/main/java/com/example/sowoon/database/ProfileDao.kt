package com.example.sowoon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.User

@Dao
interface ProfileDao {

    @Insert
    fun insertProfile(profile: Profile)

    @Query("SELECT * FROM ProfileTable WHERE userId=:userId")
    fun getProfile(userId: Int): Profile?

    @Query("SELECT * FROM ProfileTable")
    fun getAllProfile(): ArrayList<Profile>?


}