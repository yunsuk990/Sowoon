package com.example.sowoon

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ItemArtistsprofileBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

class ArtistsProfileRV(private val profileList: ArrayList<Profile>, var context: Context): RecyclerView.Adapter<ArtistsProfileRV.ViewHolder>() {

    lateinit var database: AppDatabase
    lateinit var storage: FirebaseStorage


    interface MyItemClickOnListener {
        fun profileArtworkClick(profile: Profile, database: AppDatabase)
    }

    private lateinit var mItemClickListener: MyItemClickOnListener

    fun itemClickListener(itemClick: MyItemClickOnListener){
        mItemClickListener = itemClick
    }

    inner class ViewHolder(val binding: ItemArtistsprofileBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(profile: Profile){
            database = AppDatabase.getInstance(context!!)!!
            storage = FirebaseStorage.getInstance()
            var userId = profile.userId
            var profileImg = database.profileDao().getProfileImg(userId)
            binding.profileIv.scaleType = (ImageView.ScaleType.FIT_XY)
            Glide.with(context).load(profileImg).into(binding.profileIv)
            var bestGallery = database.galleryDao().getBestArtwork(profile.bestArtwork.toString())
            Glide.with(context).load(bestGallery?.GalleryId).into(binding.profileArtistArtworkIv)
            binding.profileArtistAgeTv.text = profile.school
            binding.profileArtistNameTv.text = profile.name
            binding.profileArtistArtworkTv.text = bestGallery?.title
            binding.profileArtistArtworkInfoTv.text = bestGallery?.info

            binding.artistsProfile.setOnClickListener{
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame, ArtistProfileFragment().apply {
                        arguments = Bundle().apply {
                            val gson = Gson()
                            val profileJson = gson.toJson(profile)
                            putString("profile", profileJson)
                        }
                    })
                    .commitNowAllowingStateLoss()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArtistsprofileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(profileList[position])
        holder.binding.profileArtistArtworkIv.setOnClickListener {
            mItemClickListener.profileArtworkClick(profileList[position], database)
        }
    }

    override fun getItemCount(): Int = profileList.size

    fun addProfile(profile: Profile){
        profileList.clear()
        profileList.add(profile)
        notifyDataSetChanged()
    }
}