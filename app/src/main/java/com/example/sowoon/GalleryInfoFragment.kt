package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
import com.google.gson.Gson

class GalleryInfoFragment : Fragment() {

    lateinit var binding: FragmentGalleryInfoBinding
    private var gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryInfoBinding.inflate(inflater, container, false)

        val galleryJson = arguments?.getString("gallery")
        val gallery = gson.fromJson(galleryJson, Gallery::class.java)
        setGallery(gallery)

        return binding.root
    }

    private fun setGallery(gallery: Gallery){
        binding.galleryInfoIv.setImageResource(gallery.coverImg!!)
        binding.todayAlbumTitle.text = gallery.title
        binding.todayAlbumArtist.text = gallery.artist
        binding.todayAlbumInfo.text = gallery.info
    }

}