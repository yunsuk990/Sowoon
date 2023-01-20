package com.example.sowoon.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sowoon.data.entity.Gallery

@Dao
interface GalleryDao {

    @Insert
    fun insertGallery(gallery: Gallery)

    @Delete
    fun deleteGallery(gallery: Gallery)

    @Query("SELECT * FROM GalleryTable")
    fun getAllGallery(): List<Gallery>

    @Query("SELECT * FROM GalleryTable WHERE GalleryId = :galleryId")
    fun getGallery(galleryId: Int): Gallery

}