package com.example.sowoon

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
import com.google.gson.Gson

class GalleryInfoFragment : Fragment() {

    lateinit var binding: FragmentGalleryInfoBinding
    lateinit var database: AppDatabase
    var galleryId: String = ""
    var user: User? = null
    private var gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryInfoBinding.inflate(inflater, container, false)
        Log.d("database", "database")
        database = AppDatabase.getInstance(requireContext())!!
        user = User()

        val galleryJson = arguments?.getString("gallery")
        val gallery = gson.fromJson(galleryJson, Gallery::class.java)
        galleryId = gallery.GalleryId
        setGallery(gallery)
        return binding.root
    }

    private fun setGallery(gallery: Gallery){
        Glide.with(requireContext()).load(gallery.GalleryId).into(binding.galleryInfoIv)
        binding.todayAlbumTitle.text = gallery.title
        binding.todayAlbumArtist.text = gallery.artist
        binding.todayAlbumInfo.text = gallery.info
        binding.galleryInfoLikeCountTv.text = gallery.like.toString()

        var use = database.userDao().getUser(user!!.email, user!!.password)

        if(user != null){
            if(gallery.favorites?.contains(getJwt()) == true){
                binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
            }else{
                binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
            }
            initClickListener(gallery)
        }

    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt!!
    }

    private fun initClickListener(gallery: Gallery?) {
            var jwt = getJwt()
            binding.galleryInfoHeartIv.setOnClickListener {
                if(getJwt() != 0){
                    var likeList: ArrayList<Int>? = gallery?.favorites
                    if(likeList == null) likeList = ArrayList()
                    var galleryLikeCount: Int = gallery?.like!!

                    if(likeList?.contains(jwt) == true){
                        likeList?.remove(jwt)
                        galleryLikeCount -= 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
                    }else{
                        likeList?.add(jwt)
                        galleryLikeCount += 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)

                    }
                    Log.d("LikeList", likeList.toString())
                    gallery.favorites = likeList
                    gallery.like = galleryLikeCount
                    database.galleryDao().updateGallery(gallery)
                }else{
                    Toast.makeText(context, "로그인 후 이용해주시길 바랍니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun User(): User? {
        gson = Gson()
        val spf =
            requireActivity().getSharedPreferences("userProfile", AppCompatActivity.MODE_PRIVATE)
        return gson.fromJson(spf.getString("user", null), User::class.java)
    }
}