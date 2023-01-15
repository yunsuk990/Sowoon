package com.example.sowoon.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserTable")
data class User(
    var email: String,
    var name: String,
    var age: String,
    val school: String?,
    val awards: String?,
    var password: String,
    //Type Converter 적용
    val artwork: ArrayList<Gallery>? = null,
    val bestArtwork: Gallery? = null,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)
