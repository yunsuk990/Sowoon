package com.example.sowoon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sowoon.data.entity.User
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentSettingBinding
import com.example.sowoon.message.MessageMenu
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
    lateinit var database: AppDatabase
    lateinit var gson: Gson
    lateinit var storage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    var currentUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        storage = Firebase.storage
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        initClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(firebaseAuth.currentUser != null){
            currentUser = firebaseAuth.currentUser!!
        }
    }

    private fun initClickListener(){

        //로그인
        binding.settingLoginTv.setOnClickListener {
            if(currentUser != null){
                Toast.makeText(context, "이미 로그인되어있습니다.", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(context, LoginActivity::class.java))
            }
        }

        //회원가입
        binding.settingSignupTv.setOnClickListener {
            if(currentUser != null){
                Toast.makeText(context, "이미 로그인되어있습니다.", Toast.LENGTH_SHORT).show()
            }else {
                startActivity(Intent(context, SignUpActivity::class.java))
            }
        }

        //채팅방 목록
        binding.settingChat.setOnClickListener {
            startActivity(Intent(requireContext(), MessageMenu::class.java))
        }

        //로그아웃
        binding.settingLogoutTv.setOnClickListener {
            if(currentUser != null){
                logOut()
            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //내 정보
        binding.settingMyInfoTv.setOnClickListener {
            if(currentUser != null){
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame, SettingMyInfoFragment())
                    .commitNowAllowingStateLoss()
            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //화가 등록
        binding.settingSignupArtistTv.setOnClickListener {
            if(currentUser != null){
                firebaseDatabase.getReference().child("users").child(currentUser!!.uid).get().addOnSuccessListener { snapshot ->
                    var ifArtist = snapshot.getValue(UserModel::class.java)?.ifArtist
                    if (ifArtist == true) {
                        Toast.makeText(context, "이미 화가 등록 되어있습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        startActivity(Intent(activity, RegistArtistActivity::class.java))
                    }
                }
            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //회원탈퇴
        binding.settingQuitTv.setOnClickListener {
            if(currentUser != null){
                //database.userDao().deleteUser(User()!!)
                removeJwt()
                firebaseDatabase.getReference().child("users").child(currentUser!!.uid).removeValue()
                //firebaseDatabase.getReference().child("profile").child(currentUser!!.uid).removeValue()
                firebaseAuth.currentUser?.delete()
                currentUser = null
//                var use = database.userDao().getUser(user!!.email, user!!.password)
                //회원탈퇴시 좋아요 매커니즘 어떻게 할 것인지
//                if (likeGallery != null) {
//                    for( i in likeGallery){
//                        var gallery = database.galleryDao().getGallery(i)
//                        var likecount = gallery.like - 1
//                        database.galleryDao().setlikeCount(i, likecount)
//                    }
//                }
                database.galleryDao().deleteUserGallery(getJwt()!!.toInt())
                val desertRef = storage.reference.child("images/"+getJwt()!!.toInt())
                desertRef.delete().addOnSuccessListener {
                    Log.d("DELETE", "SUCCESS")
                }.addOnFailureListener{
                    Log.d("DELETE", "FAIL")
                }
                startActivity(Intent(context, MainActivity::class.java))
            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logOut(){
        if(currentUser != null){
            firebaseAuth.signOut()
            removeJwt()
            Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, MainActivity::class.java))
        }
    }

    private fun getJwt(): Int? {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }

    private fun removeJwt(){
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()
        editor.remove("jwt")
        editor.apply()
    }
}