package com.example.sowoon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sowoon.data.entity.User
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    // lateinit var database: AppDatabase //roomDB
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = Firebase.auth

        binding.signupBtn.setOnClickListener {
            signUp()
        }
    }



    private fun signUp(){
//        database = AppDatabase.getInstance(this)!!
        var email = binding.idEt.text.toString()
        var password = binding.passwordEt.text.toString()
        var name = binding.nameEt.text.toString()
        var passwordCheck = binding.passwordcheckEt.text.toString()
        var age = binding.ageEt.text.toString()

        if(firebaseAuth.currentUser == null){
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(object: OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {
                    var uid = p0.result.user?.uid
                    var userModel = UserModel()
                    userModel.jwt = uid.toString()
                    userModel.name = name
                    userModel.age = age
                    firebaseDatabase.getReference().child("users").child(uid.toString()).setValue(userModel)
                }
            }).addOnFailureListener {
                Toast.makeText(applicationContext, "이미 가입되어 있는 계정입니다.", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }else{
            Toast.makeText(applicationContext, "이미 로그인되어있습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}