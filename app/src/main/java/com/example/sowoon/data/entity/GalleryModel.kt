package com.example.sowoon.data.entity

import androidx.room.PrimaryKey

class GalleryModel {
    var galleryKey: String? = null
    var uid: String? = null
    var favorites: ArrayList<String>? = ArrayList()
    var title: String? = null
    var artist: String? = null
    var info: String? = null
    var like: Int = 0
    var likeUid: ArrayList<String> = ArrayList()
    var imagePath: String? = null
}