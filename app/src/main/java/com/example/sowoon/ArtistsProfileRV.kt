package com.example.sowoon

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ItemArtistsprofileBinding

class ArtistsProfileRV(private val profileList: ArrayList<Profile>, var context: Context): RecyclerView.Adapter<ArtistsProfileRV.ViewHolder>() {

    lateinit var database: AppDatabase

    interface MyItemClickOnListener {
        fun profileClick(profile: Profile)
        fun profileArtworkClick(profile: Profile)
    }

    private lateinit var mItemClickListener: MyItemClickOnListener

    fun itemClickListener(itemClick: MyItemClickOnListener){
        mItemClickListener = itemClick
    }

    inner class ViewHolder(val binding: ItemArtistsprofileBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(profile: Profile){
            database = AppDatabase.getInstance(context!!)!!
            var gallery = database.galleryDao().getGallery(profile.bestArtwork!!)
            binding.profileArtistNameTv.text = profile.name
            binding.profileArtistArtworkTv.text = gallery.title
            binding.profileArtistArtworkInfoTv.text = gallery.info
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArtistsprofileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(profileList[position])
        holder.binding.artistsProfile.setOnClickListener{
            mItemClickListener.profileClick(profileList[position])
        }
        holder.binding.profileArtistArtworkIv.setOnClickListener {
            mItemClickListener.profileArtworkClick(profileList[position])
        }
    }

    override fun getItemCount(): Int = profileList.size

    fun addProfile(profile: Profile){
        profileList.clear()
        profileList.add(profile)
        notifyDataSetChanged()
    }
}