package com.example.sowoon.database

import androidx.room.TypeConverter
import com.example.sowoon.data.entity.Gallery
import com.google.gson.Gson

class RoomConverter {

    @TypeConverter
    fun listToJson(value: ArrayList<Gallery>? = null): String? {
        if (value == null) return null
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String? = null): ArrayList<Gallery>? {
        if(value == null) return null
        return Gson().fromJson(value, Array<Gallery>::class.java) as? ArrayList<Gallery>
    }


    @TypeConverter
    fun fromListIntToString(intList: ArrayList<String>? = null): String? {
        if(intList == null) return null
        return intList.toString()
    }
    @TypeConverter
    fun toListIntFromString(stringList: String? = null): ArrayList<String>? {
        if(stringList == null) return null
        val result = ArrayList<String>()
        val split = stringList!!.replace("[","").replace("]","").replace(" ","").split(",")
        for (n in split) {
            try {
                result.add(n)
            } catch (e: Exception) {

            }
        }
        return result
    }

    @TypeConverter
    fun ListIntToString(intList: ArrayList<Int>? = null): String? {
        if(intList == null) return null
        return intList.toString()
    }
    @TypeConverter
    fun ListIntFromString(stringList: String? = null): ArrayList<Int>? {
        if(stringList == null) return null
        val result = ArrayList<Int>()
        val split = stringList!!.replace("[","").replace("]","").replace(" ","").split(",")
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