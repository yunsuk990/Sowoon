package com.example.sowoon

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.sowoon.data.entity.Gallery

class GalleryGVAdapter: BaseAdapter() {

    val galleryList = ArrayList<Gallery>()

    override fun getCount(): Int {
        return galleryList.size
    }

    override fun getItem(p0: Int): Any {
        return galleryList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()

    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        if(p1 == null){
            var inflater: LayoutInflater = p2?.context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            p1 = inflater.inflate(R.layout.item_artistgallery, p2, false)
        }
        return p1
    }

    fun addGallery(gallerys: ArrayList<Gallery>){
        galleryList.clear()
        galleryList.addAll(gallerys)
        notifyDataSetChanged()
    }
}