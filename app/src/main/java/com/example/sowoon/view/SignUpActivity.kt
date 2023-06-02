package com.example.sowoon.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.databinding.ActivitySignUpBinding
import com.example.sowoon.viewmodel.AuthViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    // lateinit var database: AppDatabase //roomDB
    lateinit var authViewModel: AuthViewModel
    val REQ_GALLERY = 10
    var profileURL: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = AuthViewModel()
        authViewModel.signUpSuccessLiveData.observe(this, Observer {
            if(it == true) {
                Toast.makeText(this, "회원가입하셨습니다.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }else {
                Toast.makeText(this, "회원가입 오류입니다.", Toast.LENGTH_SHORT).show()
            }
        })

        binding.signupProfileIv.setOnClickListener {
            openGallery()
        }
        binding.signupBtn.setOnClickListener {
            signUp()
        }
    }

    private fun signUp(){
        var email = binding.idEt.text.toString()
        var password = binding.passwordEt.text.toString()
        var name = binding.nameEt.text.toString()
        var passwordCheck = binding.passwordcheckEt.text.toString()
        var age = binding.ageEt.text.toString()

        authViewModel.signUp(email, password, name, age, profileURL)
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                REQ_GALLERY -> {
                    data?.data?.let { uri ->
                        profileURL = uri
                        binding.signupProfileIv.setImageURI(uri)
                        binding.signupProfileIv.scaleType = ImageView.ScaleType.FIT_XY
                    }
                }
            }
        }
    }
}