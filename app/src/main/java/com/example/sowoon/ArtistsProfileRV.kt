package com.example.sowoon

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.*
import com.example.sowoon.databinding.ItemArtistsprofileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class ArtistsProfileRV(var context: Context): RecyclerView.Adapter<ArtistsProfileRV.ViewHolder>() {

    var profileList: ArrayList<DataSnapshot> = ArrayList()
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    inner class ViewHolder(val binding: ItemArtistsprofileBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(profile: UserModel){
            var profileImg = profile.profileImg
            var profileModel = profile.profileModel
            var bestGallery = profileModel?.bestArtwork
            var snapshot1: DataSnapshot? = null

            firebaseDatabase.reference.child("images").child(profileModel?.key!!).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot1 = snapshot
                    var galleryModel: GalleryModel? = snapshot.getValue(GalleryModel::class.java)
                    binding.profileArtistArtworkTv.text = galleryModel?.title
                    binding.profileArtistArtworkInfoTv.text = galleryModel?.info

                    binding.profileIv.scaleType = (ImageView.ScaleType.FIT_XY)
                    Glide.with(context).load(profileImg).into(binding.profileIv)
                    Glide.with(context).load(bestGallery).into(binding.profileArtistArtworkIv)
                    binding.profileArtistAgeTv.text = profileModel?.school
                    binding.profileArtistNameTv.text = profile.name

//                    notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            binding.profileIv.setOnClickListener{
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

            binding.profileArtistArtworkIv.setOnClickListener {
                Log.d("clicked", "true")
                Log.d("clicked", snapshot1.toString())
                var ref = reference(snapshot1!!)
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame, GalleryInfoFragment().apply {
                        arguments = Bundle().apply {
//                                    val gson = Gson()
//                                    val galleryJson = gson.toJson(gallery)
//                                    putString("gallery", galleryJson)
                            putSerializable("gallery" ,ref)
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
        var profile: UserModel? = profileList[position].getValue(UserModel::class.java)
        if (profile != null) {
            holder.bind(profile)
        }
    }

    override fun getItemCount(): Int = profileList.size

    fun addProfile(userList: ArrayList<DataSnapshot>){
        profileList.clear()
        profileList.addAll(userList)
        notifyDataSetChanged()
    }
}