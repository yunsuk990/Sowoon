package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
import com.google.gson.Gson

class GalleryInfoFragment : Fragment() {

    lateinit var binding: FragmentGalleryInfoBinding
    lateinit var database: AppDatabase
    var galleryId: Int = -1
    var user: User? = null
    private var gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryInfoBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        user = User()

        val galleryJson = arguments?.getString("gallery")
        val gallery = gson.fromJson(galleryJson, Gallery::class.java)
        galleryId = gallery.GalleryId
        setGallery(gallery)
        if(user != null) {
            initClickListener()
        }
        return binding.root
    }

    private fun setGallery(gallery: Gallery){
        binding.galleryInfoIv.setImageResource(gallery.GalleryId!!)
        binding.todayAlbumTitle.text = gallery.title
        binding.todayAlbumArtist.text = gallery.artist
        binding.todayAlbumInfo.text = gallery.info
        binding.galleryInfoLikeCountTv.text = gallery.like.toString()

        var use = database.userDao().getUser(user!!.email, user!!.password)

        if(user != null){
            if(use?.likeGallery?.contains(gallery.GalleryId) == true){
                binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
            }else{
                binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
            }
        }

    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt!!
    }

    private fun initClickListener() {
        var jwt = getJwt()
        if(jwt == 0){
            Toast.makeText(context, "로그인 후 이용해주시길 바랍니다.", Toast.LENGTH_SHORT).show()
        }else{
            var use = database.userDao().getUser(user!!.email, user!!.password)
            binding.galleryInfoHeartIv.setOnClickListener {
                var likeList: ArrayList<Int>? = use?.likeGallery as ArrayList<Int>
                var galleryLikeCount: Int = database.galleryDao().getlikeCount(galleryId)!!

                if(binding.galleryInfoHeartIv.resources == resources.getDrawable(R.drawable.fullheart)){
                    likeList = likeList?.remove(galleryId) as ArrayList<Int>
                    galleryLikeCount -= 1
                    binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
                }else{
                    binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
                    likeList = likeList?.add(galleryId) as ArrayList<Int>
                    galleryLikeCount += 1
                }
                database.userDao().addLikeGallery(jwt, likeList as List<Int>)
                database.galleryDao().setlikeCount(galleryId, galleryLikeCount)
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