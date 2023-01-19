package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Database
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentArtistsBinding
import com.google.gson.Gson

class ArtistsFragment : Fragment() {

    lateinit var binding: FragmentArtistsBinding
    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistsBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!

        val adapter = ArtistsProfileRV(database.profileDao().getAllProfile() as ArrayList<Profile>)
        binding.artistsRv.adapter = adapter
        binding.artistsRv.layoutManager = LinearLayoutManager(context, GridLayoutManager.VERTICAL, false)

        adapter.itemClickListener(object: ArtistsProfileRV.MyItemClickOnListener{
            override fun profileClick(profile: Profile) {
                profileArtist(profile)
            }

            override fun profileArtworkClick(profile: Profile) {
                profileArtwork(profile)
            }
        })
        return binding.root
    }

    private fun profileArtist(profile: Profile){
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

    private fun profileArtwork(profile: Profile){
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                arguments = Bundle().apply {
                    val gallery = profile.bestArtwork
                    val gson = Gson()
                    val galleryJson = gson.toJson(gallery)
                    putString("gallery", galleryJson)
                }
            })
            .commitNowAllowingStateLoss()
    }
}