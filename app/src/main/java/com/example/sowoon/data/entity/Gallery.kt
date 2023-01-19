package com.example.sowoon.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "GalleryTable")
data class Gallery(
    @PrimaryKey var GalleryId: Int,
    var userId: Int,
    var title: String? = "",
    var artist: String? = "",
    var info: String? = "",
    var size: String? = "",
    var like: Int = 0
)
