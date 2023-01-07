package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.databinding.FragmentGalleryBinding
import com.example.sowoon.databinding.FragmentMainBinding

class GalleryFragment : Fragment() {

    lateinit var binding: FragmentGalleryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        var gridView = binding.galleryGv
        var gridViewAdapter = GalleryGVAdapter()

        var expGallery: MutableList<Gallery> = arrayListOf(Gallery("소운", "정은숙", "2020년 작품", R.drawable.galleryexp3), Gallery("소운", "정은숙", "2020년 작품", R.drawable.galleryexp1))
        gridViewAdapter.addGallery(expGallery as ArrayList<Gallery>)

        gridView.adapter = gridViewAdapter

        return binding.root
    }
}