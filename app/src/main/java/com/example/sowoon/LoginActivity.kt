package com.example.sowoon

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityLoginBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var database: AppDatabase
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        database = AppDatabase.getInstance(applicationContext)!!
        setContentView(binding.root)

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
        Log.d("email", email)
        Log.d("password", password)

        var user = database.userDao().getUser(email, password)
        Log.d("user", user.toString())

        if(user != null){
            saveJwt(user.id)
            saveUser(user)
            startMainActivity()
        }else{
            Toast.makeText(this, "회원정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startMainActivity() {
        createPushToken()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun saveJwt(jwt: Int) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()
        editor.putInt("jwt", jwt)
        Log.d("jwt", jwt.toString())
        editor.apply()
    }

    private fun saveUser(user: User){
        gson = Gson()
        var userJson = gson.toJson(user)
        val spf = getSharedPreferences("userProfile", MODE_PRIVATE)
        val editor = spf.edit()
        editor.putString("user", userJson)
        editor.apply()
    }

    //FCM token 생성
    fun createPushToken(){
        var token = FirebaseMessaging.getInstance().token
        database.userDao().insertPushToken(getJwt()!!, token.toString())
    }

    private fun getJwt(): Int? {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }
}