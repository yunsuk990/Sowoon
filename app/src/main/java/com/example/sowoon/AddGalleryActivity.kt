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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    lateinit var firebaseAuth: FirebaseAuth
    val REQ_GALLERY = 10
    var URI: Uri? = null
    lateinit var storage: FirebaseStorage
    var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = AppDatabase.getInstance(this)!!
        storage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        //binding.addgalleryArtistInput.text = user?.name

        binding.addgalleryIv.setOnClickListener {
            openGallery()
        }
        binding.addgalleryBtn.setOnClickListener {
            upload()
        }
    }

    override fun onStart() {
        super.onStart()
        currentUser = firebaseAuth.currentUser
    }

    private fun upload(){
        if(currentUser == null){
            Toast.makeText(this, "로그인 후 이용해주시길 바랍니다.", Toast.LENGTH_SHORT).show()
        }
        var title = binding.addgalleryTitleInput.text.toString()
        var info = binding.addgalleryInfoInput.text.toString()
        var galleryPath = URI?.lastPathSegment.toString()
        if(URI != null){
            var mountainImageRef: StorageReference? = storage?.reference?.child("images")?.child(galleryPath)
            Log.d("FirebaseUri", URI.toString())
            mountainImageRef?.putFile(URI!!)?.addOnSuccessListener {
                mountainImageRef.downloadUrl.addOnSuccessListener { url ->
                    Log.d("FirebaseUri", url.toString())
                    //val Gallery = Gallery(url.toString(), galleryPath, getJwt()!!,null ,title, user?.name, info, null, 0)
                    //database.galleryDao().insertGallery(Gallery)
                    var intent: Intent = Intent()
                    intent.putExtra("BestArtworkURL", url.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }?.addOnFailureListener{
                Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                Log.d("FirebaseUri", "FAIL", it)
                finish()
            }
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
                        URI = uri
                        binding.addgalleryIv.setImageURI(uri)
                        binding.addgalleryIv.scaleType = ImageView.ScaleType.FIT_XY
                    }
                }
            }
        }
    }
}

