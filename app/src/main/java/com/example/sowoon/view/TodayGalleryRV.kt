package com.example.sowoon.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.databinding.ItemTodayalbumBinding

class TodayGalleryRV(var context: Context): RecyclerView.Adapter<TodayGalleryRV.ViewHolder>() {

    private var galleryList = ArrayList<GalleryModel>()

    interface MyItemOnClickListener{
        fun galleryClick(gallery: GalleryModel)
    }

    private lateinit var mItemClickListener: MyItemOnClickListener

    fun setMyItemClickListener(itemClickListener: MyItemOnClickListener){
        mItemClickListener = itemClickListener
    }

    fun addGallery(gallerys: ArrayList<GalleryModel>){
        galleryList.clear()
        galleryList.addAll(gallerys)
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ItemTodayalbumBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(gallery: GalleryModel){
            Glide.with(context).load(gallery.imagePath).into(binding.todayAlbumIv)
            binding.todayAlbumTitle.text = gallery.title
            binding.todayAlbumArtist.text = gallery.artist
            binding.todayAlbumInfo.text= gallery.info
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding  = ItemTodayalbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(galleryList[position])
        holder.binding.todayAlbumIv.setOnClickListener {
            mItemClickListener.galleryClick(galleryList[position])
        }
    }

    override fun getItemCount(): Int = galleryList.size
}