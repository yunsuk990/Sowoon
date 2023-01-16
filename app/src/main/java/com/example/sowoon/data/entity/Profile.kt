package com.example.sowoon.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProfileTable",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class Profile(
    @PrimaryKey var userId: Int,
    val name: String,
    val school: String?,
    val awards: String?,
    val artwork: ArrayList<Gallery>? = null,
    val bestArtwork: Gallery? = null
)
