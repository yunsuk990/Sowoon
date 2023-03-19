package com.example.sowoon

import android.content.Context
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.databinding.ItemArtistgalleryBinding
import com.google.firebase.database.DataSnapshot

class ArtistGalleryGVAdapter(var galleryList: ArrayList<DataSnapshot>?, var context: Context): BaseAdapter() {


    interface MyItemClickListener {
        fun artworkClick(gallery: DataSnapshot)
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
        var gallery = galleryList?.get(p0)?.getValue(GalleryModel::class.java)
        Glide.with(context).load(gallery?.imagePath).centerCrop().into(binding.galleryIv)
        binding.galleryIv.setOnClickListener {
            mItemClickListener.artworkClick(galleryList!![p0])
        }
        return binding.root
    }


}