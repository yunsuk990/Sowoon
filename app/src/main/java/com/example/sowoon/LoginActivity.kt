package com.example.sowoon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sowoon.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    //lateinit var database: AppDatabase
    lateinit var gson: Gson
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        //database = AppDatabase.getInstance(applicationContext)!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
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

        if(email != "" || password != ""){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    saveJwt(task.result.user!!.uid)
                }else{
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
            startActivity(Intent(this, MainActivity::class.java))
//            var user = database.userDao().getUser(email, password)
//            saveJwt(user.id)
//            saveUser(user)
        }
    }


    private fun saveJwt(jwt: String) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()
        editor.putString("jwt", jwt)
        Log.d("jwt", jwt.toString())
        editor.apply()
    }
//
//    private fun saveUser(user: User){
//        gson = Gson()
//        var userJson = gson.toJson(user)
//        val spf = getSharedPreferences("userProfile", MODE_PRIVATE)
//        val editor = spf.edit()
//        editor.putString("user", userJson)
//        editor.apply()
//    }

//    private fun getJwt(): Int? {
//        val spf = getSharedPreferences("auth", MODE_PRIVATE)
//        var jwt = spf?.getString("jwt", null)
//        return jwt
//    }
}