package com.example.sowoon

import android.content.Intent
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
import com.example.sowoon.databinding.FragmentGalleryBinding
import com.example.sowoon.databinding.FragmentMainBinding
import com.google.gson.Gson

class GalleryFragment : Fragment() {

    lateinit var binding: FragmentGalleryBinding
    lateinit var database: AppDatabase
    lateinit var gson: Gson
    var jwt: Int = -1
    var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        jwt = getJwt()!!

        binding.addGallery.setOnClickListener {
            //나중에 앨범에서 사진 가져오기
            if(jwt != 0){
                startActivity(Intent(context, AddGalleryActivity::class.java))
            }else{
                Toast.makeText(context, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }
        initGridView()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        user = User()
    }

    private fun initGridView(){
        var gridView = binding.galleryGv
        var gridViewAdapter = GalleryGVAdapter()
        var expGallery: List<Gallery> = database.galleryDao().getAllGallery()
        gridViewAdapter.addGallery(expGallery as ArrayList<Gallery>)
        gridViewAdapter.itemClickListener(object: GalleryGVAdapter.MyItemClickListener{
            override fun artworkClick(gallery: Gallery) {
                ArtworkClick(gallery)
            }
        })
        gridView.adapter = gridViewAdapter
    }

    private fun ArtworkClick(gallery: Gallery){
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

    private fun addGallery(){
        user = database.userDao().getUser(user!!.email, user!!.password)
        Log.d("ifArtist", user?.ifArtist.toString())
        if(user?.ifArtist == true){
            //사용자가 사진 등록
            var exp = Gallery(R.drawable.galleryexp3, jwt , "모나리자","정은숙", "2020년 작품",null, 0)
            database.galleryDao().insertGallery(exp)
        }else{
            Toast.makeText(context, "화가 등록 후 이용 가능합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getJwt(): Int? {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }

    private fun User(): User? {
        gson = Gson()
        val spf =
            requireActivity().getSharedPreferences("userProfile", AppCompatActivity.MODE_PRIVATE)
        return gson.fromJson(spf.getString("user", null), User::class.java)
    }
}