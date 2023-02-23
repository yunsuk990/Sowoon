package com.example.sowoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivitySettingMyLikeBinding
import com.google.gson.Gson

class SettingMyLikeActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingMyLikeBinding
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingMyLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = AppDatabase.getInstance(this)!!
        setGridView()


    }

    private fun setGridView() {
        var datas = database.galleryDao().getUserGallery(getJwt()) as ArrayList<Gallery>
        var gridView = binding.myLikeGv
        var adapter = ArtistGalleryGVAdapter(datas as ArrayList<Gallery>, this)
        adapter.itemClickListener(object: ArtistGalleryGVAdapter.MyItemClickListener{
            override fun artworkClick(gallery: Gallery) {
                ArtworkClick(gallery)
            }
        })
        gridView.adapter = adapter
    }

    private fun ArtworkClick(gallery: Gallery) {
            supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val galleryJson = gson.toJson(gallery)
                    putString("gallery", galleryJson)
                }
            })
            .commitNowAllowingStateLoss()
    }


    private fun getJwt(): Int {
        val spf = getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt!!
    }

}