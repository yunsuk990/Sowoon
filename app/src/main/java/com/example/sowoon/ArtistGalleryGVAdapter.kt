package com.example.sowoon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.databinding.ItemArtistgalleryBinding

class ArtistGalleryGVAdapter(var galleryList: ArrayList<Gallery>?, var context: Context): BaseAdapter() {


    interface MyItemClickListener {
        fun artworkClick(gallery: Gallery)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun itemClickListener(itemClick: MyItemClickListener){
        mItemClickListener = itemClick
    }

    override fun getCount(): Int = galleryList?.size!!

    override fun getItem(p0: Int): Any {
        return galleryList?.get(p0)!!
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var binding = ItemArtistgalleryBinding.inflate(LayoutInflater.from(p2?.context), p2, false)
        var galleryUrl = galleryList?.get(p0)?.GalleryId!!
        Glide.with(context).load(galleryUrl).centerCrop().into(binding.galleryIv)
        binding.galleryIv.setOnClickListener {
            mItemClickListener.artworkClick(galleryList!![p0])
        }
        return binding.root
    }


}