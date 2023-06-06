package com.example.sowoon.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel: ViewModel() {


    var firebaseRepository: FirebaseRepository
    var signUpSuccessLiveData: MutableLiveData<Boolean>
    var loginSuccessLiveData: MutableLiveData<String>
    var currentUserLiveData: MutableLiveData<FirebaseUser>
    var userProfileLiveData: MutableLiveData<UserModel>


    init {
        firebaseRepository = FirebaseRepository()
        signUpSuccessLiveData = firebaseRepository.signUpSuccessLiveData
        loginSuccessLiveData = firebaseRepository.loginSuccessLiveData
        currentUserLiveData = firebaseRepository.currentUserLiveData
        userProfileLiveData = firebaseRepository.userProfileLiveData
    }

    fun signUp(email: String, password: String, name: String, age: String, profileURL: Uri?)
        = firebaseRepository.signUp(email ,password, name, age, profileURL)

    fun login(email: String, password: String) = firebaseRepository.login(email,password)

    fun createPushToken() = firebaseRepository.createPushToken()

    fun getUserProfile(uid: String) = firebaseRepository.getUserProfile(uid)

    fun logOut() = firebaseRepository.logOut()

    fun deleteAccount(uid: String, jwt: String) = firebaseRepository.deleteAccount(uid, jwt)

}