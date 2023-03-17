package com.example.sowoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sowoon.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Sowoon)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        initBottomNavgitaion()
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            var uid = firebaseAuth.currentUser?.uid
            createPushToken(uid!!)
        }
    }

    private fun initBottomNavgitaion(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, HomeFragment())
            .commitNowAllowingStateLoss()

        binding.bottomMenu.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.mainFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, HomeFragment())
                        .commitNowAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.ArtistsFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, ArtistsFragment())
                        .commitNowAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.GalleryFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, GalleryFragment())
                        .commitNowAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.SettingFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, SettingFragment())
                        .commitNowAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

            }
            false
        }
    }

    //FCM token 생성
    fun createPushToken(uid: String){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            Log.d("token", task.result.toString())
            //database.userDao().insertPushToken(getJwt()!!, task.result.toString())
            var map: MutableMap<String, Any> = HashMap()
            map.put("pushToken", task.result.toString())
            firebaseDatabase.getReference().child("users").child(uid).updateChildren(map)
        }
    }
}