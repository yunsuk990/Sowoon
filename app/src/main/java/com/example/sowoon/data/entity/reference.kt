package com.example.sowoon.data.entity

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class reference(gallery: DataSnapshot) : Serializable {
    var gallery: DataSnapshot = gallery
}