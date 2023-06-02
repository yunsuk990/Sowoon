package com.example.sowoon.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sowoon.MainActivity
import com.example.sowoon.R
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentGalleryBinding
import com.example.sowoon.view.adapter.GalleryGVAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class GalleryFragment : Fragment() {

    lateinit var binding: FragmentGalleryBinding
    lateinit var database: AppDatabase
    lateinit var gson: Gson
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    var currentUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

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
        //var expGallery: ArrayList<Gallery>? = database.galleryDao().getAllGallery() as? ArrayList<Gallery>
        firebaseDatabase.getReference().child("images").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var galleryList: ArrayList<GalleryModel>? = ArrayList()
                for(item in snapshot.children){
                    var galleryModel = item.getValue(GalleryModel::class.java)
                    if (galleryModel != null) {
                        galleryList!!.add(galleryModel)
                    }
                }
                if (galleryList != null) {
                    gridViewAdapter.addGallery(galleryList)
                }
                gridView.adapter = gridViewAdapter
                gridViewAdapter.itemClickListener(object: GalleryGVAdapter.MyItemClickListener{
                    override fun artworkClick(gallery: GalleryModel) {
                        ArtworkClick(gallery)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun ArtworkClick(gallery: GalleryModel){
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