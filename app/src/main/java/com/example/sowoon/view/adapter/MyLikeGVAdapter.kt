package com.example.sowoon.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.net.toUri
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.databinding.ItemArtistgalleryBinding

class MyLikeGVAdapter(): BaseAdapter() {

    val galleryList = ArrayList<Gallery>()

    interface MyItemClickListener {
        fun artworkClick(gallery: Gallery)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun itemClickListener(itemClick: MyItemClickListener){
        mItemClickListener = itemClick
    }

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
        var binding = ItemArtistgalleryBinding.inflate(LayoutInflater.from(p2?.context), p2, false)
        binding.galleryIv.setImageURI(galleryList[p0].GalleryId!!.toUri())
        binding.galleryIv.setOnClickListener {
            mItemClickListener.artworkClick(galleryList[p0])
        }
        return binding.root
    }

    fun addGallery(gallerys: ArrayList<Gallery>){
        galleryList.clear()
        galleryList.addAll(gallerys)
        notifyDataSetChanged()
    }
}