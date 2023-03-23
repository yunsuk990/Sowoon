package com.example.sowoon

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.*
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityRegistArtistBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class RegistArtistActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistArtistBinding
    lateinit var database: AppDatabase
    lateinit var storage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var gson: Gson
    val Best_GALLERY = 20
    var bestImageURL: Uri? = null
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

        binding.registBtn.setOnClickListener {
            registInfo()
        }
        binding.myInfoBestArtworkIv.setOnClickListener{
            openBestGallery()
        }
    }

    override fun onStart() {
        super.onStart()
        currentUser = firebaseAuth.currentUser
    }

    private fun registInfo(){
        uploadImage(bestImageURL)
        //Firebase의 비동기식 처리로 인한 처리 불가
//        var firebaseprofilepath: String? = uploadImage(profileURL)
//        var firebasebestImagepath: String? = uploadImage(bestImageURL)
//            var map: MutableMap<String, Any> = HashMap()
//            map.put("ifArtist", true)
//            if (firebaseprofilepath != null) {
//                map.put("profileImg", firebaseprofilepath!!)
//            }
//            firebaseDatabase.getReference().child("users").child(currentUser!!.uid).updateChildren(map)

        Toast.makeText(this, "등록되었습니다." ,Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun uploadImage(imageUri: Uri?){
        var userModel: UserModel? = null
        firebaseDatabase.reference.child("users").child(currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userModel = snapshot.getValue(UserModel::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        var key = imageUri?.lastPathSegment.toString()
        var galleryModel = GalleryModel()
        var mountainImageRef: StorageReference? = storage?.reference?.child("images")?.child(key)
        mountainImageRef?.putFile(imageUri!!)?.addOnCompleteListener{
            mountainImageRef.downloadUrl.addOnSuccessListener { url ->
                var path = url.toString()
                var profileModel = ProfileModel()

                profileModel.bestArtwork = path
                profileModel.awards = binding.myInfoAwardsInput.text.toString()
                profileModel.school = binding.myInfoSchoolInput.text.toString()
                profileModel.key = key

                galleryModel.galleryKey = key
                galleryModel.uid = currentUser?.uid
                galleryModel.artist = userModel?.name
                galleryModel.imagePath = path

//                var title: String? = null
//                var info: String? = null
//                var likeUid: ArrayList<String> = ArrayList()
                var map: MutableMap<String, Any> = HashMap()
                map.put("ifArtist", true)
                map.put("profileModel", profileModel)
                map.put("profieImgKey", key)
                firebaseDatabase.reference.child("users").child(currentUser!!.uid).updateChildren(map)
                firebaseDatabase.reference.child("images").child(key).setValue(galleryModel)
            }
        }


    }

    private fun openBestGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, Best_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Best_GALLERY -> {
                    data?.data?.let {  uri ->
                        bestImageURL = uri
                        binding.myInfoBestArtworkIv.scaleType = ImageView.ScaleType.FIT_XY
                        binding.myInfoBestArtworkIv.setImageURI(uri)
                    }
                }
            }
        }
    }
}