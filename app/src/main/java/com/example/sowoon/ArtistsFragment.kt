package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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


        val adapter = ArtistsProfileRV()
        val profile1 = Profile("정은숙")
        adapter.addProfile(profile1)
        binding.artistsRv.adapter = adapter
        binding.artistsRv.layoutManager = LinearLayoutManager(context, GridLayoutManager.HORIZONTAL, false)

        return binding.root
    }
}