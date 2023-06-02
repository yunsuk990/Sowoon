package com.example.sowoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sowoon.databinding.ActivityMainBinding
import com.example.sowoon.view.ArtistsFragment
import com.example.sowoon.view.GalleryFragment
import com.example.sowoon.view.HomeFragment
import com.example.sowoon.view.SettingFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Sowoon)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavgitaion()
    }

    private fun initBottomNavgitaion(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, HomeFragment())
            .commitNowAllowingStateLoss()

        binding.bottomMenu.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.mainFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, HomeFragment())
                        .commitNowAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.ArtistsFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, ArtistsFragment())
                        .commitNowAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.GalleryFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, GalleryFragment())
                        .commitNowAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.SettingFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, SettingFragment())
                        .commitNowAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

            }
            false
        }
    }
}