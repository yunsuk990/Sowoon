package com.example.sowoon.database

import androidx.room.TypeConverter
import com.example.sowoon.data.entity.Gallery
import com.google.gson.Gson

class RoomConverter {
    @TypeConverter
    fun listToJson(value: MutableList<Gallery>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String?): MutableList<Gallery>? {
        if(value != null){
            return Gson().fromJson(value, Array<Gallery>::class.java).toList() as MutableList<Gallery>
        }
        return null
    }

    @TypeConverter
    fun intToList(value: ArrayList<Int>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun ListToInt(value: String?): ArrayList<Int>? {
        return Gson().fromJson(value, Array<Int>::class.java) as ArrayList<Int>
    }

//    @TypeConverter
//    fun galleryToJson(value: Gallery?): String? {
//        return Gson().toJson(value)
//    }
//
//    @TypeConverter
//    fun jsonGalleryToList(value: String?): Gallery? {
//        return Gson().fromJson(value, Gallery::class.java)
//    }

}