package com.example.sowoon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.databinding.ItemTodayalbumBinding
import java.net.URL
import kotlin.coroutines.coroutineContext

class TodayGalleryRV(var context: Context): RecyclerView.Adapter<TodayGalleryRV.ViewHolder>() {

    private var galleryList = ArrayList<Gallery>()

    interface MyItemOnClickListener{
        fun galleryClick(gallery: Gallery)
    }

    private lateinit var mItemClickListener: MyItemOnClickListener

    fun setMyItemClickListener(itemClickListener: MyItemOnClickListener){
        mItemClickListener = itemClickListener
    }

    fun addGallery(gallerys: ArrayList<Gallery>){
        galleryList.clear()
        galleryList.addAll(gallerys)
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ItemTodayalbumBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(gallery: Gallery){
            var url = gallery.GalleryId
            Log.d("url",url.toString())
            Glide.with(context).asBitmap().load(url).override(300,300).centerCrop().into(binding.todayAlbumIv)
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