package com.example.sowoon.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.sowoon.R
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.databinding.FragmentHomeGalleryBinding

class HomeGalleryFragment(var item: GalleryModel) : Fragment() {

    lateinit var binding: FragmentHomeGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeGalleryBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init(){
        Glide.with(this@HomeGalleryFragment).load(item.imagePath).into(binding.todayAlbumIv)
        binding.todayAlbumTitle.text = item.title
        binding.todayAlbumArtist.text = item.artist
        binding.todayAlbumInfo.text= item.info
        binding.homeGalleryContainer.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, GalleryInfoFragment().apply {
                    var galleryJson = gson.toJson(item)
                    var bundle = Bundle()
                    bundle.putString("gallery", galleryJson)
                    arguments = bundle
                }).commitNowAllowingStateLoss()
        }
    }
}