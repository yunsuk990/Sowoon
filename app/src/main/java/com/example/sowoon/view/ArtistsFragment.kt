package com.example.sowoon.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentArtistsBinding
import com.example.sowoon.viewmodel.ArtistViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArtistsFragment : Fragment() {

    lateinit var binding: FragmentArtistsBinding
    lateinit var artistViewModel: ArtistViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistsBinding.inflate(inflater, container, false)
        artistViewModel = ArtistViewModel()
        artistViewModel.artistsProfileLiveData.observe(viewLifecycleOwner, Observer {
            val adapter = ArtistsProfileRV(requireContext())
            binding.artistsRv.adapter = adapter
            binding.artistsRv.layoutManager = LinearLayoutManager(context, GridLayoutManager.VERTICAL, false)
            adapter.addProfile(it)
            binding.artistsProgressbar.visibility = View.INVISIBLE
        })
        artistViewModel.getArtistProfile()
        return binding.root
    }
}