package com.example.sowoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sowoon.databinding.ActivityAddGalleryBinding

class AddGalleryActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}