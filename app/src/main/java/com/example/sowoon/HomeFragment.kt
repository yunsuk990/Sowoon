package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentMainBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView(){
        val todayGalleryAdapter = TodayGalleryRV(requireContext()!!)
        binding.mainTodayAlbumRv.adapter = todayGalleryAdapter
        binding.mainTodayAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false )

        //테스트 이미지
        val galleryList = database.galleryDao().getAllGallery() as ArrayList<Gallery>
        todayGalleryAdapter.addGallery(galleryList!!)

        todayGalleryAdapter.setMyItemClickListener(object: TodayGalleryRV.MyItemOnClickListener{
            override fun galleryClick(gallery: Gallery) {
                galleryOnClick(gallery)
            }
        })
    }

    private fun galleryOnClick(gallery: Gallery){
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val galleryJson = gson.toJson(gallery)
                    putString("gallery", galleryJson)
                }
            })
            .commitNowAllowingStateLoss()
    }
}