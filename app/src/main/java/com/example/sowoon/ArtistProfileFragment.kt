package com.example.sowoon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.databinding.FragmentArtistProfileBinding
import com.google.gson.Gson

class ArtistProfileFragment : Fragment() {

    lateinit var binding: FragmentArtistProfileBinding
    private var gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistProfileBinding.inflate(inflater, container, false)

        val profileJson = arguments?.getString("profile")
        val profile = gson.fromJson(profileJson, Profile::class.java)
        setProfile(profile)

        return binding.root
    }

    private fun setProfile(profile: Profile){
        binding.artistsProfileSchoolInput.text = profile.school
        binding.artistsProfileAwardsInput.text = profile.awards
    }

}