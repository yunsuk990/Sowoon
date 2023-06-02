package com.example.sowoon.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.databinding.ItemArtistgalleryBinding

class ArtistGalleryGVAdapter(var context: Context): BaseAdapter() {

    var galleryList: ArrayList<GalleryModel>? = ArrayList()

    fun addGalleryList(gallerys: ArrayList<GalleryModel>){
        galleryList?.addAll(gallerys)
        notifyDataSetChanged()
    }

    interface MyItemClickListener {
        fun artworkClick(gallery: GalleryModel)
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
        var gallery = galleryList?.get(p0)
        Glide.with(context).load(gallery?.imagePath).centerCrop().into(binding.galleryIv)
        binding.galleryIv.setOnClickListener {
            mItemClickListener.artworkClick(gallery!!)
        }
        return binding.root
    }
}