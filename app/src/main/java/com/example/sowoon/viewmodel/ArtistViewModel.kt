package com.example.sowoon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.repository.FirebaseRepository

class ArtistViewModel: ViewModel() {

    var artistsProfileLiveData: MutableLiveData<ArrayList<UserModel>>
    val firebaseRepository: FirebaseRepository = FirebaseRepository()

    init {
        artistsProfileLiveData = firebaseRepository.artistsProfileLiveData
    }

    fun getArtistProfile() = firebaseRepository.getArtistProfile()





}