package com.example.sowoon.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserTable")
data class User(
    var email: String,
    var name: String,
    var age: String,
    var password: String,
    //Type Converter 적용
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)
