package com.example.sowoon.data.entity

data class Profile(
    val name: String,
    val age: Int? = null,
    val school: String?,
    val awards: String?,
    val artwork: ArrayList<Gallery>? = null,
    val bestArtwork: Gallery? = null
)
