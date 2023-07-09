package com.example.sowoon.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sowoon.MainActivity
import com.example.sowoon.R
import com.example.sowoon.data.entity.*
import com.example.sowoon.databinding.FragmentArtistProfileBinding
import com.example.sowoon.view.adapter.ArtistGalleryGVAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class ArtistProfileFragment : Fragment() {

    lateinit var binding: FragmentArtistProfileBinding
    private var gson = Gson()
    lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistProfileBinding.inflate(inflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance()

        val profileJson = arguments?.getString("profile")
        var profile = gson.fromJson(profileJson, UserModel::class.java)
        setProfile(profile)
        setGridView(profile)
        return binding.root
    }

    private fun setProfile(profile: UserModel){
        binding.artistsProfileName.text = profile.name + " 화가"
        binding.artistsProfileSchoolInput.text = profile.profileModel?.school
        binding.artistsProfileAwardsInput.text = profile.profileModel?.awards
        binding.artistsProfileAgeInput.text = profile.age
        Glide.with(requireContext()).load(profile.profileImg).into(binding.artistsProfileIv)
    }

    private fun setGridView(profile: UserModel) {
        var gridView = binding.artistsProfileGv
        var adapter = ArtistGalleryGVAdapter(requireContext())
        adapter.itemClickListener(object: ArtistGalleryGVAdapter.MyItemClickListener{
            override fun artworkClick(gallery: GalleryModel) {
                ArtworkClick(gallery)
            }
        })
        gridView.adapter = adapter
        firebaseDatabase.reference.child("images").orderByChild("artist").equalTo(profile.name).addValueEventListener(object:
            ValueEventListener {
            var galleryList = ArrayList<GalleryModel>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for( item in snapshot.children){
                    var galleryModel = item.getValue(GalleryModel::class.java)
                    if (galleryModel != null) {
                        galleryList!!.add(galleryModel)
                    }
                }
                if (galleryList != null) {
                    adapter.addGalleryList(galleryList)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun ArtworkClick(gallery: GalleryModel) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                arguments = Bundle().apply {
                    var galleryJson = gson.toJson(gallery)
                    var bundle = Bundle()
                    bundle.putString("gallery", galleryJson)
                    arguments = bundle
                }
            })
            .commitNowAllowingStateLoss()
    }

}