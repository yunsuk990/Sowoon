package com.example.sowoon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentArtistProfileBinding
import com.google.gson.Gson

class ArtistProfileFragment : Fragment() {

    lateinit var binding: FragmentArtistProfileBinding
    private var gson = Gson()
    lateinit var database: AppDatabase

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

        return binding.root
    }

    private fun setProfile(profile: Profile){
        var user = User()
        var age = database.userDao().getUser(user!!.email, user!!.password)?.age
        binding.artistsProfileName.text = profile.name + " 화가"
        binding.artistsProfileSchoolInput.text = profile.school
        binding.artistsProfileAwardsInput.text = profile.awards
        binding.artistsProfileAgeInput.text = age.toString()
    }

    private fun User(): User? {
        gson = Gson()
        val spf =
            requireActivity().getSharedPreferences("userProfile", AppCompatActivity.MODE_PRIVATE)
        return gson.fromJson(spf.getString("user", null), User::class.java)
    }

}