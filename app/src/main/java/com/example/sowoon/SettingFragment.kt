package com.example.sowoon

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sowoon.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
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
        binding.settingLoginTv.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }

        binding.settingSignupTv.setOnClickListener {
            startActivity(Intent(context, SignUpActivity::class.java))
        }

        binding.settingLogoutTv.setOnClickListener {
            logOut()
        }
    }

    private fun logOut(){
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()
        val user = spf.getInt("jwt", -1)
        if( user != -1){
            editor.remove("jwt")
            editor.apply()
            Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, MainActivity::class.java))
        }
    }




}