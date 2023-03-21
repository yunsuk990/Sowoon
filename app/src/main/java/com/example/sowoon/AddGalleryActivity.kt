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
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.data.entity.User
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityAddGalleryBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
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
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var storage: FirebaseStorage
    var currentUser: FirebaseUser? = null
    var userModel: UserModel? = null

    val REQ_GALLERY = 10
    var imageURL: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = AppDatabase.getInstance(this)!!
        storage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

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
        firebaseDatabase.reference.child("users").child(currentUser!!.uid).get().addOnSuccessListener {
                snapshot ->
            userModel = snapshot.getValue(UserModel::class.java)
            binding.addgalleryArtistInput.text = userModel?.name
        }
    }

    private fun upload(){
        if(currentUser == null){
            Toast.makeText(this, "로그인 후 이용해주시길 바랍니다.", Toast.LENGTH_SHORT).show()
        }
        var galleryModel: GalleryModel = GalleryModel()
        var title = binding.addgalleryTitleInput.text.toString()
        var info = binding.addgalleryInfoInput.text.toString()
        var galleryPath = imageURL?.lastPathSegment.toString()

//        var metadata = storageMetadata {
//            setCustomMetadata("title", title)
//            setCustomMetadata("info", info)
//            setCustomMetadata("uid", currentUser?.uid)
//        }

        //이미지 Storage 및 Database 업로드
        if(imageURL != null){
            var mountainImageRef: StorageReference? = storage?.reference?.child("images")?.child(galleryPath)
            mountainImageRef?.putFile(imageURL!!)?.addOnSuccessListener {
                mountainImageRef.downloadUrl.addOnSuccessListener { url ->
                    galleryModel.galleryKey = galleryPath
                    galleryModel.uid = currentUser?.uid
                    galleryModel.title = title
                    galleryModel.info = info
                    galleryModel.imagePath = url.toString()
                    galleryModel.artist = userModel?.name

                    firebaseDatabase.getReference().child("images").child(galleryPath).setValue(galleryModel)
                }
            }?.addOnFailureListener{
                Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                Log.d("FirebaseUri", "FAIL", it)
            }
            finish()
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
                        imageURL = uri
                        binding.addgalleryIv.setImageURI(uri)
                        binding.addgalleryIv.scaleType = ImageView.ScaleType.FIT_XY
                    }
                }
            }
        }
    }
}

