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
    fun gettingListFromString(genreIds: String): ArrayList<Int> {
        val list = ArrayList<Int>()

        val array = genreIds.split(",".toRegex()).dropLastWhile {
            it.isEmpty()
        }.toTypedArray()

        for (s in array) {
            if (s.isNotEmpty()) {
                list.add(s.toInt())
            }
        }
        return list
    }

    @TypeConverter
    fun writingStringFromList(list: ArrayList<Int>?): String {
        var genreIds=""
        for (i in list!!) genreIds += ",$i"
        return genreIds
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