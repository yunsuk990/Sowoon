package com.example.sowoon

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentArtistProfileBinding
import com.google.gson.Gson

class ArtistProfileFragment : Fragment() {

    lateinit var binding: FragmentArtistProfileBinding
    private var gson = Gson()
    lateinit var database: AppDatabase
    var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistProfileBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        val profileJson = arguments?.getString("profile")
        val profile = gson.fromJson(profileJson, Profile::class.java)
        setProfile(profile)
        //setGridView()
        return binding.root
    }

    private fun setProfile(profile: Profile){
        var user = User()
        var age = database.userDao().getUser(user!!.email, user!!.password)?.age
        binding.artistsProfileName.text = profile.name + " 화가"
        binding.artistsProfileSchoolInput.text = profile.school
        binding.artistsProfileAwardsInput.text = profile.awards
        binding.artistsProfileAgeInput.text = age.toString()
        var uri = database.profileDao().getProfileImg(getJwt())
        Glide.with(requireContext()).load(uri).into(binding.artistsProfileIv)
    }

    private fun User(): User? {
        gson = Gson()
        val spf =
            requireActivity().getSharedPreferences("userProfile", AppCompatActivity.MODE_PRIVATE)
        return gson.fromJson(spf.getString("user", null), User::class.java)
    }

//    private fun setGridView() {
//        var datas = database.galleryDao().getUserGallery(getJwt()) as ArrayList<Gallery>
//        var gridView = binding.artistsProfileGv
//        var adapter = ArtistGalleryGVAdapter(datas as ArrayList<Gallery>, requireContext())
//        adapter.itemClickListener(object: ArtistGalleryGVAdapter.MyItemClickListener{
//            override fun artworkClick(gallery: Gallery) {
//                ArtworkClick(gallery)
//            }
//        })
//        gridView.adapter = adapter
//
//    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt!!
    }

    private fun ArtworkClick(gallery: Gallery) {
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