package com.example.sowoon.repository

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.sowoon.data.entity.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseRepository {

    private var firebaseDatabase: FirebaseDatabase
    private var firebaseAuth: FirebaseAuth
    private var firebaseStorage: FirebaseStorage
    var signUpSuccessLiveData: MutableLiveData<Boolean>
    var loginSuccessLiveData: MutableLiveData<String>
    var currentUserLiveData: MutableLiveData<FirebaseUser>
    var userProfileLiveData: MutableLiveData<UserModel>
    var artistsProfileLiveData: MutableLiveData<ArrayList<UserModel>>

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        signUpSuccessLiveData = MutableLiveData()
        loginSuccessLiveData = MutableLiveData()
        currentUserLiveData = MutableLiveData()
        userProfileLiveData = MutableLiveData()
        artistsProfileLiveData = MutableLiveData()
        if(firebaseAuth.currentUser != null){
            currentUserLiveData.value = firebaseAuth.currentUser
        }
    }


    fun signUp(email: String, password: String, name: String, age: String, profileURL: Uri?){
        var mountainImageRef: StorageReference? = firebaseStorage?.reference?.child("images")?.child(profileURL?.lastPathSegment.toString())
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(object: OnCompleteListener<AuthResult> {
            override fun onComplete(p0: Task<AuthResult>) {
                if(profileURL != null){
                    mountainImageRef?.putFile(profileURL!!)?.addOnSuccessListener {
                        mountainImageRef.downloadUrl.addOnSuccessListener { url ->
                            var uid = p0.result.user?.uid
                            var userModel = UserModel()
                            userModel.jwt = uid.toString()
                            userModel.name = name
                            userModel.age = age
                            userModel.profileImg = url.toString()
                            firebaseDatabase.getReference().child("users").child(uid.toString()).setValue(userModel)
                            signUpSuccessLiveData.value = true
                        }
                    }?.addOnFailureListener{
                        signUpSuccessLiveData.value = false
                    }
                }else{
                    var uid = p0.result.user?.uid
                    var userModel = UserModel()
                    userModel.jwt = uid.toString()
                    userModel.name = name
                    userModel.age = age
                    userModel.profileImg = null
                    firebaseDatabase.getReference().child("users").child(uid.toString()).setValue(userModel).addOnSuccessListener {
                        signUpSuccessLiveData.value = true
                    }.addOnFailureListener{
                        signUpSuccessLiveData.value = false
                    }
                }
            }
        }).addOnFailureListener {
            signUpSuccessLiveData.value = false
        }
    }

    fun login(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                loginSuccessLiveData.value = task.result.user?.uid
                Log.d("firerepLogin", "success")
            }else{
                loginSuccessLiveData.value = null
                Log.d("firerepLogin", task.exception.toString())
            }
        }
    }

    //FCM token 생성
    fun createPushToken(){
        var uid = firebaseAuth.currentUser?.uid.toString()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            Log.d("token", task.result.toString())
            //database.userDao().insertPushToken(getJwt()!!, task.result.toString())
            var map: MutableMap<String, Any> = HashMap()
            map.put("pushToken", task.result.toString())
            firebaseDatabase.getReference().child("users").child(uid).updateChildren(map)
        }
    }

    //유저 프로필 모델 가져오기
    fun getUserProfile(uid: String) {
        firebaseDatabase.reference.child("users").child(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue(UserModel::class.java)
                Log.d("user", user?.name.toString())
                userProfileLiveData.value = user
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //로그아웃
    fun logOut(){
        firebaseAuth.signOut()
    }


    //계정 탈퇴
    fun deleteAccount(uid: String, jwt: String){
        firebaseDatabase.getReference().child("users").child(uid).removeValue()
        firebaseAuth.currentUser?.delete()
        val desertRef = firebaseStorage.reference.child("images/"+ jwt)
        desertRef.delete().addOnSuccessListener {
            Log.d("DELETE", "SUCCESS")
        }.addOnFailureListener{
            Log.d("DELETE", "FAIL")
        }
    }

    fun getArtistProfile(){
        firebaseDatabase.getReference().child("users").orderByChild("ifArtist").equalTo(true).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userList: ArrayList<UserModel> = ArrayList()
                for(item in snapshot.children){
                    var profileModel = item.getValue(UserModel::class.java)
                    if (profileModel != null) {
                        userList.add(profileModel)
                    }
                }
                Log.d("userList", userList.toString())
                artistsProfileLiveData.value = userList
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }




}