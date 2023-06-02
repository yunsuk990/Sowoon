package com.example.sowoon.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.MainActivity
import com.example.sowoon.R
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

class HomeFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    lateinit var database: AppDatabase
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseStorage: FirebaseStorage
    private var gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        initRecyclerView()
        return binding.root
    }


    private fun initRecyclerView(){
        val todayGalleryAdapter = TodayGalleryRV(requireContext()!!)
        binding.mainTodayAlbumRv.adapter = todayGalleryAdapter
        binding.mainTodayAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false )

        firebaseDatabase.getReference().child("images").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var galleryModelList: ArrayList<GalleryModel>? = ArrayList()
                for(item in snapshot.children){
                    Log.d("item", item.toString())
                    Log.d("itemkey", item.key.toString())
                    var galleryModel: GalleryModel? = item.getValue(GalleryModel::class.java)
                    if (galleryModel != null) {
                        galleryModelList?.add(galleryModel)
                    }
                }
                if (galleryModelList != null) {
                    todayGalleryAdapter.addGallery(galleryModelList)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        todayGalleryAdapter.setMyItemClickListener(object: TodayGalleryRV.MyItemOnClickListener {
            override fun galleryClick(gallery: GalleryModel) {
                galleryOnClick(gallery)
            }
        })
    }

    private fun galleryOnClick(gallery: GalleryModel){
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                var galleryJson = gson.toJson(gallery)
                var bundle = Bundle()
                bundle.putString("gallery", galleryJson)
                arguments = bundle
            })
            .commitNowAllowingStateLoss()
    }
}