package com.example.sowoon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    lateinit var database: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupBtn.setOnClickListener {
            signUp()
        }
    }



    private fun signUp(){
        database = AppDatabase.getInstance(this)!!
        var email = binding.idEt.text.toString()
        var password = binding.passwordEt.text.toString()
        var passwordCheck = binding.passwordcheckEt.text.toString()
        var age = binding.ageEt.text.toString()

        if(database.userDao().getUser(email, password) == null){
            database.userDao().insertUser(User(email, binding.nameEt.text.toString(), age, password))
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }else{
            Toast.makeText(applicationContext, "이미 가입되어 있는 계정입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}