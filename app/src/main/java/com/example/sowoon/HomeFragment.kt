package com.example.sowoon

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.data.entity.reference
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentMainBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import java.io.Serializable

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
                var galleryModelList: ArrayList<DataSnapshot>? = ArrayList()
                for(item in snapshot.children){
                    Log.d("item", item.toString())
                    Log.d("itemkey", item.key.toString())
                    galleryModelList?.add(item)
                }
                if (galleryModelList != null) {
                    todayGalleryAdapter.addGallery(galleryModelList)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        todayGalleryAdapter.setMyItemClickListener(object: TodayGalleryRV.MyItemOnClickListener{
            override fun galleryClick(gallery: DataSnapshot) {
                galleryOnClick(gallery)
            }
        })
    }

    private fun galleryOnClick(gallery: DataSnapshot){
        var ref = reference(gallery)
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                arguments = Bundle().apply {
                   putSerializable("gallery", ref)
//                    val galleryJson = gson.toJson(gallery)
//                    putString("gallery", galleryJson)
                }
            })
            .commitNowAllowingStateLoss()
    }
}