package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Database
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentArtistsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class ArtistsFragment : Fragment() {

    lateinit var binding: FragmentArtistsBinding
    lateinit var database: AppDatabase
    lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistsBinding.inflate(inflater, container, false)
        //database = AppDatabase.getInstance(requireContext())!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.getReference().child("users").orderByChild("ifArtist").equalTo(true).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var userList: ArrayList<DataSnapshot> = ArrayList()
                for(item in snapshot.children){
                    userList.add(item)
                }
                val adapter = ArtistsProfileRV(requireContext())
                adapter.addProfile(userList)
                binding.artistsRv.adapter = adapter
                binding.artistsRv.layoutManager = LinearLayoutManager(context, GridLayoutManager.VERTICAL, false)

                adapter.itemClickListener(object: ArtistsProfileRV.MyItemClickOnListener{
                    override fun profileArtworkClick(profile: UserModel) {
                        profileArtwork(profile)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return binding.root
    }



    //화가 대표작 클릭 시
    private fun profileArtwork(profile: UserModel){
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