package com.example.sowoon

import android.os.Bundle
import android.util.Log
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
        binding.galleryInfoIv.setImageResource(gallery.GalleryId!!)
        binding.todayAlbumTitle.text = gallery.title
        binding.todayAlbumArtist.text = gallery.artist
        binding.todayAlbumInfo.text = gallery.info
        binding.galleryInfoLikeCountTv.text = gallery.like.toString()

        var use = database.userDao().getUser(user!!.email, user!!.password)

        if(user != null){
            Log.d("likeGallery", use?.likeGallery.toString())
            if(use?.likeGallery?.contains(gallery.GalleryId) == true){
                binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
            }else{
                binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
            }
            initClickListener(use)
        }

    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt!!
    }

    private fun initClickListener(use: User?) {
            var jwt = getJwt()
            binding.galleryInfoHeartIv.setOnClickListener {
                if(getJwt() != 0){
                    var likeList: ArrayList<Int>? = use?.likeGallery as? ArrayList<Int>
                    if(likeList == null) likeList = ArrayList()
                    var galleryLikeCount: Int = database.galleryDao().getlikeCount(galleryId)!!

                    if(likeList?.contains(galleryId) == true){
                        likeList?.remove(galleryId)
                        galleryLikeCount -= 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
                    }else{
                        likeList?.add(galleryId)
                        galleryLikeCount += 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)

                    }
                    Log.d("LikeList", likeList.toString())
                    database.userDao().addLikeGallery(jwt, likeList)
                    database.galleryDao().setlikeCount(galleryId, galleryLikeCount)
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