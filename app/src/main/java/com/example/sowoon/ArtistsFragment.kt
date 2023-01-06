package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.databinding.FragmentArtistsBinding
import com.google.gson.Gson

class ArtistsFragment : Fragment() {

    lateinit var binding: FragmentArtistsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistsBinding.inflate(inflater, container, false)


        val adapter = ArtistsProfileRV()
        val profile1 = Profile("정은숙", "홍익대학교", "한국미술대회 1위")
        adapter.addProfile(profile1)
        binding.artistsRv.adapter = adapter
        binding.artistsRv.layoutManager = LinearLayoutManager(context, GridLayoutManager.HORIZONTAL, false)

        adapter.itemClickListener(object: ArtistsProfileRV.MyItemClickOnListener{
            override fun profileClick(profile: Profile) {
                profileArtist(profile)
            }
        })
        return binding.root
    }

    private fun profileArtist(profile: Profile){
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, ArtistProfileFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val profileJson = gson.toJson(profile)
                    putString("profile", profileJson)
                }
            })
            .commitNowAllowingStateLoss()
    }
}