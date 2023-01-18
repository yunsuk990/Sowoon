package com.example.sowoon.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//화가 등록 유저
@Entity(tableName = "ProfileTable")
data class Profile(
    @PrimaryKey var userId: Int,
    val school: String?,
    val awards: String?,
    val artwork: ArrayList<Gallery>? = null,
    val bestArtwork: Gallery? = null
)
