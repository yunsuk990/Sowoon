package com.example.sowoon.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.sowoon.MainActivity
import com.example.sowoon.databinding.ActivityLoginBinding
import com.example.sowoon.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var gson: Gson
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        authViewModel = AuthViewModel()
        setContentView(binding.root)
        authViewModel.loginSuccessLiveData.observe(this, Observer {
            if(it != null){
                saveJwt(it)
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                Toast.makeText(this, "아이디나 비밀번호를 잘못 입력하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        })
        binding.loginBtn.setOnClickListener {
            login()
        }
    }

    private fun login(){
        if(binding.idEt.toString().isEmpty()){
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.passwordEt.toString().isEmpty()){
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        var email = binding.idEt.text.toString()
        var password = binding.passwordEt.text.toString()
        if(email != "" || password != ""){
            authViewModel.login(email, password)
            authViewModel.createPushToken()
        }
    }


    private fun saveJwt(jwt: String) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()
        editor.putString("jwt", jwt)
        Log.d("jwt", jwt)
        editor.apply()
    }
}