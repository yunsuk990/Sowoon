package com.example.sowoon.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//일반 유저
@Entity(tableName = "UserTable")
data class User(
    var email: String,
    var name: String,
    var age: String,
    var password: String,
    var ifArtist: Boolean = false,
    var likeGallery: ArrayList<Int>?,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

