package com.example.sowoon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.databinding.ItemArtistsprofileBinding

class ArtistsProfileRV(private val profileList: ArrayList<Profile>): RecyclerView.Adapter<ArtistsProfileRV.ViewHolder>() {

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

            binding.profileArtistNameTv.text = profile.name
//            binding.profileArtistArtworkTv.text = profile.bestArtwork?.title
//            binding.profileArtistArtworkInfoTv.text = profile.bestArtwork?.info
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