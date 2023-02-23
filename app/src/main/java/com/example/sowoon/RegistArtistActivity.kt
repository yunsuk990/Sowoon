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
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityRegistArtistBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

class RegistArtistActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistArtistBinding
    lateinit var database: AppDatabase
    lateinit var storage: FirebaseStorage
    lateinit var gson: Gson
    var GalleryId: String = ""
    val REQ_GALLERY = 10
    var URI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = Firebase.storage
        database = AppDatabase.getInstance(this)!!
        binding.myInfoName.text = getName()

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

    private fun registInfo(){
        var jwt = getJwt()
        if(jwt == 0){
            Toast.makeText(this, "로그인 후 이용해주시기 바랍니다." ,Toast.LENGTH_SHORT).show()
            return
        }
        var school = binding.myInfoSchoolInput.text.toString()
        var awards = binding.myInfoAwardsInput.text.toString()
        var bestArtwork: String? = null
        var profileImg: String? = null

        Toast.makeText(this, "등록되었습니다." ,Toast.LENGTH_SHORT).show()

        if(GalleryId != ""){
            var gallery = database.galleryDao().getGallery(GalleryId)
            var galleryPath = gallery.galleryPath
            bestArtwork = galleryPath
        }
        if(URI != null){
            var mountainImageRef: StorageReference? = storage?.reference?.child("images")?.child(getJwt().toString())?.child("Profile")?.child("profile.png")
            Log.d("FirebaseUri", URI.toString())
            mountainImageRef?.putFile(URI!!)?.addOnSuccessListener {
                mountainImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("registProfileImage", uri.toString())
                    profileImg = uri.toString()
                    var profile: Profile = Profile(school,awards,getName(),profileImg ,bestArtwork, jwt!!)
                    Log.d("profile", profile.toString())
                    database.userDao().ifArtistRegist(jwt!!,true)
                    database.profileDao().insertProfile(profile)
                }
            }?.addOnFailureListener{
                Log.d("registProfileImage", "FAIL", it)
            }
        }
        finish()
    }

    private fun User(): User? {
        gson = Gson()
        val spf = getSharedPreferences("userProfile", MODE_PRIVATE)
        return gson.fromJson(spf.getString("user", null), User::class.java)
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_GALLERY)
    }

    private fun getName(): String?{
        var use = User()
        var User = database.userDao().getUser(use!!.email, use!!.password)
        return User?.name.toString()
    }

    private fun getJwt(): Int? {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    GalleryId = data?.getStringExtra("GalleryId")!!
                    Log.d("GalleryId", GalleryId.toString())
                    binding.myInfoBestArtworkIv.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(this).load(GalleryId).into(binding.myInfoBestArtworkIv)
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