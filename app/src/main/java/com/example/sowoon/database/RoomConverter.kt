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
    fun fromListIntToString(intList: List<Int>? = null): String? {
        if(intList == null) return null
        return intList.toString()
    }
    @TypeConverter
    fun toListIntFromString(stringList: String? = null): List<Int>? {
        if(stringList == null) return null
        val result = ArrayList<Int>()
        val split =stringList!!.replace("[","").replace("]","").replace(" ","").split(",")
        for (n in split) {
            try {
                result.add(n.toInt())
            } catch (e: Exception) {

            }
        }
        return result
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