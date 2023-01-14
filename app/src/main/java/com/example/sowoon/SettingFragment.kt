package com.example.sowoon

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentSettingBinding
import com.google.gson.Gson

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
    lateinit var database: AppDatabase
    lateinit var gson: Gson
    var jwt: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        jwt = getJwt()!!
        initClickListener()
        return binding.root
    }

    private fun initClickListener(){
        binding.settingLoginTv.setOnClickListener {
            if(jwt != 0){
                Toast.makeText(context, "이미 로그인되어있습니다.", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(context, LoginActivity::class.java))
            }
        }

        binding.settingSignupTv.setOnClickListener {
            if(jwt != 0){
                Toast.makeText(context, "이미 로그인되어있습니다.", Toast.LENGTH_SHORT).show()
            }else {
                startActivity(Intent(context, SignUpActivity::class.java))
            }
        }

        binding.settingLogoutTv.setOnClickListener {
            if(jwt != 0){
                logOut()
            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.settingMylikeTv.setOnClickListener {
            if(jwt != 0){

            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.settingMyInfoTv.setOnClickListener {
            if(jwt != 0){

            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.settingSignupArtistTv.setOnClickListener {
            if(jwt != 0){

            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //회원탈퇴
        binding.settingQuitTv.setOnClickListener {
            if(jwt != 0){
                database.userDao().deleteUser(getUser()!!)
                removeJwt()
                startActivity(Intent(context, MainActivity::class.java))
            }else{
                Toast.makeText(context, "로그인 후 이용하시기 바랍니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logOut(){
        val user = getJwt()
        if( user != 0){
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

    private fun getUser(): User? {
        gson = Gson()
        val spf =
            requireActivity().getSharedPreferences("userProfile", AppCompatActivity.MODE_PRIVATE)
        var user = spf.getString("user", null)
        return gson.fromJson(user, User::class.java)
    }
}