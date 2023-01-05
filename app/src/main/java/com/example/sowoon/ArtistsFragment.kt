package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sowoon.databinding.FragmentArtistsBinding
import com.example.sowoon.databinding.FragmentMainBinding

class ArtistsFragment : Fragment() {

    lateinit var binding: FragmentArtistsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }
}