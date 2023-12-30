package com.example.sowoon.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
            binding.signupProgressbar.visibility = View.INVISIBLE
        })

        binding.signupProfileIv.setOnClickListener {
            openGallery()
        }
        binding.signupBtn.setOnClickListener {
            signUp()
        }

        textWatcher()
    }

    private fun signUp(){
        var email = binding.idEt.text.toString()
        var password = binding.passwordEt.text.toString()
        var name = binding.nameEt.text.toString()
        var passwordCheck = binding.passwordcheckEt.text.toString()
        var age = binding.ageEt.text.toString()


        if(name.isEmpty()){
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }else if (age.isEmpty()){
            Toast.makeText(this, "나이를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }else if(email.isEmpty()){
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }else if(password.isEmpty()){
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }else if(passwordCheck.isEmpty()){
            Toast.makeText(this, "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }else if(password != passwordCheck){
            Toast.makeText(this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
        }else{
            if(!email.contains('@')){
                Toast.makeText(this, "이메일 형식으로 작성해주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            binding.signupProgressbar.visibility = View.VISIBLE
            authViewModel.signUp(email, password, name, age, profileURL)
        }
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

    private fun textWatcher(){
        binding.idEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if(binding.idEt.text?.contains('@') == false){
                    binding.signupIdEt.error = "이메일 형식으로 작성해주세요."
                }else{
                    binding.signupIdEt.error = null
                }
            }
        })
    }
}