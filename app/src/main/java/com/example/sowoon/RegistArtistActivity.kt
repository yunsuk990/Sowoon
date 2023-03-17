package com.example.sowoon

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.ProfileModel
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityRegistArtistBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

class RegistArtistActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistArtistBinding
    lateinit var database: AppDatabase
    lateinit var storage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var gson: Gson
    var bestArtwork: String = ""
    val REQ_GALLERY = 10
    var URI: Uri? = null
    var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = Firebase.storage
        database = AppDatabase.getInstance(this)!!
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        //binding.myInfoName.text = getName()

        binding.myInfoIv.setOnClickListener{
            openGallery()
        }

        binding.registBtn.setOnClickListener {
            registInfo()
        }
        binding.myInfoBestArtworkIv.setOnClickListener{
            startActivityForResult(Intent(this, AddGalleryActivity::class.java), 0)
        }
    }

    override fun onStart() {
        super.onStart()
        currentUser = firebaseAuth.currentUser
    }

    private fun registInfo(){
        if(currentUser == null){
            Toast.makeText(this, "로그인 후 이용해주시기 바랍니다." ,Toast.LENGTH_SHORT).show()
            return
        }
        var school = binding.myInfoSchoolInput.text.toString()
        var awards = binding.myInfoAwardsInput.text.toString()

        Toast.makeText(this, "등록되었습니다." ,Toast.LENGTH_SHORT).show()

//        if(bestArtwork != ""){
//            //var gallery = database.galleryDao().getGallery(bestArtwork.toString())
//            //var galleryPath = gallery.galleryPath
//            //bestArtwork = galleryPath
//        }

        if(URI != null){
            var mountainImageRef: StorageReference? = storage?.reference?.child("profile")?.child(currentUser!!.uid)?.child("profile.png")
            Log.d("FirebaseUri", URI.toString())
            mountainImageRef?.putFile(URI!!)?.addOnSuccessListener {
                mountainImageRef.downloadUrl.addOnSuccessListener{ url ->
                    var profileModel = ProfileModel()
                    profileModel.profileImg = url.toString()
                    profileModel.bestArtwork = bestArtwork
                    profileModel.awards = awards
                    profileModel.school = school
                    Log.d("profileImg",url.toString())
                    var map: MutableMap<String, Any> = HashMap()
                    map.put("ifArtist", true)
                    firebaseDatabase.getReference().child("profile").child(currentUser!!.uid).setValue(profileModel)
                    firebaseDatabase.getReference().child("users").child(currentUser!!.uid).updateChildren(map)
//                    database.userDao().ifArtistRegist(jwt!!,true)
//                    database.profileDao().insertProfile(profile)
                }
            }
        }
        finish()
    }


    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_GALLERY)
    }

    private fun getJwt(): String? {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        var jwt = spf?.getString("jwt", null)
        return jwt
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    bestArtwork = data?.getStringExtra("BestArtworkURL")!!
                    Log.d("BestArtworkURL", bestArtwork.toString())
                    binding.myInfoBestArtworkIv.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(this).load(bestArtwork).into(binding.myInfoBestArtworkIv)
                    binding.myInfoBestArtworkIv.scaleType = ImageView.ScaleType.FIT_XY
                }
                REQ_GALLERY -> {
                    data?.data?.let { uri ->
                        URI = uri
                        binding.myInfoIv.setImageURI(uri)
                        binding.myInfoIv.scaleType = ImageView.ScaleType.FIT_XY
                    }
                }

            }
        }
    }
}