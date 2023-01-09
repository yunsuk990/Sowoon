package com.example.sowoon

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sowoon.data.entity.User
import com.example.sowoon.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            login()
        }
    }

    private fun login(){
        if(binding.loginIdEt.toString().isEmpty()){
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.loginPasswordEt.toString().isEmpty()){
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        var email = binding.loginIdEt.toString()
        var password = binding.loginPasswordEt.toString()

        //DB 조회..
        var expUser = User(1, "yunsuk990@naver.com", "최윤석",25, "yunsuk123")

        if(expUser != null){
            saveJwt(expUser.id)
            startMainActivity()
        }else{
            Toast.makeText(this, "회원정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun saveJwt(jwt: Int) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()
        editor.putInt("jwt", jwt)
        editor.apply()
    }
}