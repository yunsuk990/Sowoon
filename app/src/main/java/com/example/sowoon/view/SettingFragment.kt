package com.example.sowoon.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.sowoon.MainActivity
import com.example.sowoon.R
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentSettingBinding
import com.example.sowoon.message.MessageMenu
import com.example.sowoon.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
    lateinit var database: AppDatabase
    lateinit var gson: Gson
    lateinit var authViewModel: AuthViewModel
    var currentUser: FirebaseUser? = null
    var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = AuthViewModel()

        // 로그인 여부 확인
        authViewModel.currentUserLiveData.observe(this, Observer {
            if(it != null){
                currentUser = it
                authViewModel.getUserProfile(it.uid)
            }else{
                Toast.makeText(context, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show()
            }
        })

        //프로필정보 가져오기
        authViewModel.userProfileLiveData.observe(this, Observer {
            user = it
            init()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        initClickListener()
        return binding.root
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
                var ifArtist = user?.ifArtist
                if (ifArtist == true) {
                    Toast.makeText(context, "이미 화가 등록 되어있습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(activity, RegistArtistActivity::class.java))
                }
            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //회원탈퇴
        binding.settingQuitTv.setOnClickListener {
            if(currentUser != null){
                var jwt = getJwt()
                authViewModel.deleteAccount(currentUser!!.uid, jwt!!)
                removeJwt()

                //회원탈퇴시 좋아요 매커니즘 어떻게 할 것인지
//                if (likeGallery != null) {
//                    for( i in likeGallery){
//                        var gallery = database.galleryDao().getGallery(i)
//                        var likecount = gallery.like - 1
//                        database.galleryDao().setlikeCount(i, likecount)
//                    }
//                }
                startActivity(Intent(context, MainActivity::class.java))
            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(){
        binding.settingNameTitle.text = user?.name
        binding.settingAgeTitle.text = user?.age + "살"
    }

    private fun logOut(){
        if(currentUser != null){
            authViewModel.logOut()
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