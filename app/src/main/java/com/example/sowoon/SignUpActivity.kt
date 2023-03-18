package com.example.sowoon

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    // lateinit var database: AppDatabase //roomDB
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseStorage: FirebaseStorage
    val REQ_GALLERY = 10
    var profileURL: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        binding.signupProfileIv.setOnClickListener {
            openGallery()
        }

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
            var mountainImageRef: StorageReference? = firebaseStorage?.reference?.child("images")?.child(profileURL?.lastPathSegment.toString())
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(object: OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {
                    mountainImageRef?.putFile(profileURL!!)?.addOnSuccessListener {
                        mountainImageRef.downloadUrl.addOnSuccessListener { url ->
                            var uid = p0.result.user?.uid
                            var userModel = UserModel()
                            userModel.jwt = uid.toString()
                            userModel.name = name
                            userModel.age = age
                            userModel.profileImg = url.toString()
                            firebaseDatabase.getReference().child("users").child(uid.toString()).setValue(userModel)
                        }
                    }
                }
            }).addOnFailureListener {
                Toast.makeText(applicationContext, "이미 가입되어 있는 계정입니다.", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }else{
            Toast.makeText(applicationContext, "이미 로그인되어있습니다.", Toast.LENGTH_SHORT).show()
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
}