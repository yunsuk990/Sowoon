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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson

class GalleryFragment : Fragment() {

    lateinit var binding: FragmentGalleryBinding
    lateinit var database: AppDatabase
    lateinit var gson: Gson
    lateinit var firebaseAuth: FirebaseAuth
    var currentUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        firebaseAuth = FirebaseAuth.getInstance()

        binding.addGallery.setOnClickListener {
            //나중에 앨범에서 사진 가져오기
            if(currentUser != null){
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
        if(firebaseAuth.currentUser != null){
            currentUser = firebaseAuth.currentUser
        }
    }

    private fun initGridView(){
        var gridView = binding.galleryGv
        var gridViewAdapter = GalleryGVAdapter(requireContext())
        var expGallery: ArrayList<Gallery>? = database.galleryDao().getAllGallery() as? ArrayList<Gallery>
        Log.d("expGallery", expGallery.toString())
        gridViewAdapter.addGallery(expGallery!!)
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


    private fun getJwt(): String? {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getString("jwt", null)
        return jwt
    }
}