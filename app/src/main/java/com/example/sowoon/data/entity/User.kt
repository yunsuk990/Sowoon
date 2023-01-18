package com.example.sowoon.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//일반 유저
@Entity(tableName = "UserTable",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Profile::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("id"),
            onDelete = ForeignKey.CASCADE
        )
    ))
data class User(
    var email: String,
    var name: String,
    var age: String,
    var password: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)
