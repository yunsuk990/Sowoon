package com.example.sowoon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sowoon.data.entity.Profile

@Dao
interface ProfileDao {

    @Insert
    fun insertProfile(profile: Profile)

    @Query("SELECT * FROM ProfileTable WHERE userId=:userId")
    fun getProfile(userId: Int): Profile?

    @Query("SELECT * FROM ProfileTable ")
    fun getAllProfile(): MutableList<Profile>?

    @Update
    fun updateProfile(profile: Profile)



}