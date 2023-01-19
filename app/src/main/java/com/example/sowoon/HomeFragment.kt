package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.databinding.FragmentMainBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView(){
        val todayGalleryAdapter = TodayGalleryRV()
        binding.mainTodayAlbumRv.adapter = todayGalleryAdapter
        binding.mainTodayAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false )

        //테스트 이미지
        val gallery1 = Gallery(R.drawable.galleryexp3, 1, "모나리자","정은숙", "2020년 작품",null, 10)
        val galleryList: MutableList<Gallery> = arrayListOf(gallery1)
        todayGalleryAdapter.addGallery(galleryList as ArrayList<Gallery>)

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