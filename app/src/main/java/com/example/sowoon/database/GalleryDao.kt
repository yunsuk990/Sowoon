package com.example.sowoon.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sowoon.data.entity.Gallery
import org.checkerframework.checker.guieffect.qual.UIPackage

@Dao
interface GalleryDao {

    @Insert
    fun insertGallery(gallery: Gallery)

    @Delete
    fun deleteGallery(gallery: Gallery)

    @Query("SELECT * FROM GalleryTable")
    fun getAllGallery(): List<Gallery>?

    @Query("SELECT * FROM GalleryTable WHERE GalleryId = :galleryId")
    fun getGallery(galleryId: String): Gallery

    @Query("UPDATE GalleryTable SET `like`= :like WHERE GalleryId=:galleryId")
    fun setlikeCount(galleryId: String, like: Int)

    @Query("SELECT `like` FROM GalleryTable WHERE GalleryId=:galleryId")
    fun getlikeCount(galleryId: String): Int?

    @Update
    fun updateGallery(gallery: Gallery)

    @Query("DELETE FROM GalleryTable WHERE userId=:id")
    fun deleteUserGallery(id: Int)

}