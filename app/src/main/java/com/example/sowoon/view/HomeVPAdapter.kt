package com.example.sowoon.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sowoon.data.entity.GalleryModel

class HomeVPAdapter(fragment: Fragment, val galleryModelList: ArrayList<GalleryModel>): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = galleryModelList.size

    override fun createFragment(position: Int): Fragment {
        return HomeGalleryFragment(galleryModelList[position])
    }
}