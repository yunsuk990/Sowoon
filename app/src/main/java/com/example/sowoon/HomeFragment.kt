package com.example.sowoon

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentMainBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import java.io.Serializable

class HomeFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    lateinit var database: AppDatabase
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseStorage: FirebaseStorage
    var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView(){
        val todayGalleryAdapter = TodayGalleryRV(requireContext()!!)
        binding.mainTodayAlbumRv.adapter = todayGalleryAdapter
        binding.mainTodayAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false )

        //테스트 이미지
//        val galleryList = database.galleryDao().getAllGallery() as ArrayList<Gallery>
//        todayGalleryAdapter.addGallery(galleryList!!)


        var mountainImageRef: StorageReference? = firebaseStorage?.reference?.child("images")
        mountainImageRef?.listAll()?.addOnSuccessListener(object: OnSuccessListener<ListResult>{
            override fun onSuccess(p0: ListResult?) {
                var galleryList: ArrayList<StorageReference> = ArrayList()
                for(img in p0?.items!!){
                    galleryList.add(img)
                }
                todayGalleryAdapter.addGallery(galleryList!!)
                Log.d("galleryList", galleryList.toString())
            }
        })


        todayGalleryAdapter.setMyItemClickListener(object: TodayGalleryRV.MyItemOnClickListener{
            override fun galleryClick(gallery: StorageReference) {
                galleryOnClick(gallery)
            }
        })
    }

    private fun galleryOnClick(gallery: StorageReference){
        var ref = reference(gallery)
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                arguments = Bundle().apply {
                   putSerializable("gallery", ref)
                }
            })
            .commitNowAllowingStateLoss()
    }

    class reference(gallery: StorageReference) : Serializable {
        var gallery = gallery
    }
}