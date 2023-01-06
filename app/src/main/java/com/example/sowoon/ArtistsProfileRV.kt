package com.example.sowoon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sowoon.databinding.ItemArtistsprofileBinding

class ArtistsProfileRV: RecyclerView.Adapter<ArtistsProfileRV.ViewHolder>() {

    private val profileList = ArrayList<Profile>()

    inner class ViewHolder(val binding: ItemArtistsprofileBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(profile: Profile){
            binding.profileArtistNameTv.text = profile.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArtistsprofileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(profileList[position])

    }

    override fun getItemCount(): Int = profileList.size

    fun addProfile(profile: Profile){
        profileList.clear()
        profileList.add(profile)
        notifyDataSetChanged()
    }
}