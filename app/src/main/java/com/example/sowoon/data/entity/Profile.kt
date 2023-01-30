package com.example.sowoon.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//화가 등록 유저
@Entity(tableName = "ProfileTable",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ))
data class Profile(
    var school: String? = "",
    var awards: String? = "",
    var name: String? = "",
    var artwork: List<Gallery>?,
    var bestArtwork: Int? = null,
    @PrimaryKey var userId: Int
)
