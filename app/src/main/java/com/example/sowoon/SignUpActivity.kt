package com.example.sowoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sowoon.data.entity.User
import com.example.sowoon.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }



    private fun signUp(){

        var expUser = User(1, "yunsuk990@naver.com", "최윤석",25, "yunsuk123")
        //DB중복 아이디 확인.. 없으면 회원가입


    }
}