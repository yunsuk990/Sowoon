package com.example.sowoon

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityAddGalleryBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception
import java.net.URI

class AddGalleryActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddGalleryBinding
    lateinit var gson: Gson
    lateinit var database: AppDatabase
    val REQ_GALLERY = 10
    var URI: Uri? = null
    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var user = User()
        database = AppDatabase.getInstance(this)!!
        storage = Firebase.storage

        binding.addgalleryArtistInput.text = user?.name

        binding.addgalleryIv.setOnClickListener {
            openGallery()
        }
        binding.addgalleryBtn.setOnClickListener {
            upload(user)
        }
    }

    private fun upload(user: User?){
        if(user == null){
            Toast.makeText(this, "로그인 후 이용해주시길 바랍니다.", Toast.LENGTH_SHORT).show()
        }
        var title = binding.addgalleryTitleInput.text.toString()
        var info = binding.addgalleryInfoInput.text.toString()
        var mountainImageRef: StorageReference? = storage?.reference?.child("images")?.child(getJwt().toString())?.child(URI?.lastPathSegment.toString())
        mountainImageRef?.putFile(URI!!)?.addOnSuccessListener {
            mountainImageRef.downloadUrl.addOnSuccessListener { url ->
                Log.d("FirebaseUri", url.toString())
                val Gallery = Gallery(url.toString(), getJwt()!!,null ,title, user?.name, info, null, 0)
                database.galleryDao().insertGallery(Gallery)
            }
        }?.addOnFailureListener{
            Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
            Log.d("FirebaseUri", "FAIL", it)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                REQ_GALLERY -> {
                    data?.data?.let { uri ->
                        URI = uri
                        binding.addgalleryIv.setImageURI(uri)
                        binding.addgalleryIv.scaleType = ImageView.ScaleType.FIT_XY
                    }
                }
            }
        }
    }


    private fun getJwt(): Int? {
        val spf = getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }
}

