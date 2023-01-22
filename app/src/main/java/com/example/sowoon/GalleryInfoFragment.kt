package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
import com.google.gson.Gson

class GalleryInfoFragment : Fragment() {

    lateinit var binding: FragmentGalleryInfoBinding
    lateinit var database: AppDatabase
    private var gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryInfoBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!

        val galleryJson = arguments?.getString("gallery")
        val gallery = gson.fromJson(galleryJson, Gallery::class.java)
        setGallery(gallery)

        return binding.root
    }

    private fun setGallery(gallery: Gallery){
        binding.galleryInfoIv.setImageResource(gallery.GalleryId!!)
        binding.todayAlbumTitle.text = gallery.title
        binding.todayAlbumArtist.text = gallery.artist
        binding.todayAlbumInfo.text = gallery.info
        binding.galleryInfoLikeCountTv.text = gallery.like.toString()

        if(database.userDao().getLikeGallery(getJwt())?.contains(gallery.GalleryId) == true){
            binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
        }else{
            binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
        }
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt!!
    }

    private fun initClickListener() {
        if(getJwt() == 0){
            Toast.makeText(context, "로그인 후 이용해주시길 바랍니다.", Toast.LENGTH_SHORT).show()
        }else{
            binding.galleryInfoHeartIv.setOnClickListener {
                
            }
        }
    }

}