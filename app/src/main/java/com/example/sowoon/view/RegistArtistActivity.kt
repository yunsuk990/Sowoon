package com.example.sowoon.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.sowoon.data.entity.*
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityRegistArtistBinding
import com.example.sowoon.viewmodel.ArtistViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

class RegistArtistActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistArtistBinding
    lateinit var storage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var gson: Gson
    lateinit var artistViewModel: ArtistViewModel
    val Best_GALLERY = 20
    var bestImageURL: Uri? = null
    var currentUser: FirebaseUser? = null
    var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = Firebase.storage
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()


        artistViewModel = ArtistViewModel()
        artistViewModel.userProfileLiveData.observe(this, Observer{
            if(it != null){
                binding.myInfoName.text = it.name
                userModel = it
            }
        })
        binding.registBtn.setOnClickListener {
            if(bestImageURL == null){
                Toast.makeText(this, "대표작을 등록해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                registInfo()
            }
        }
        binding.myInfoBestArtworkIv.setOnClickListener{
            openBestGallery()
        }
    }

    override fun onStart() {
        super.onStart()
        currentUser = firebaseAuth.currentUser
        artistViewModel.getUserProfile(currentUser?.uid.toString())
    }

    private fun registInfo(){
        uploadImage(bestImageURL)
        Toast.makeText(this, "등록되었습니다." ,Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun uploadImage(imageUri: Uri?){
        var key = imageUri?.lastPathSegment.toString()
        var mountainImageRef: StorageReference? = storage?.reference?.child("images")?.child(key)
        mountainImageRef?.putFile(imageUri!!)?.addOnCompleteListener{
            mountainImageRef.downloadUrl.addOnSuccessListener { url ->
                var path = url.toString()
                var profileModel = ProfileModel()
                var galleryModel = GalleryModel()

                profileModel.bestArtwork = path
                profileModel.awards = binding.myInfoAwardsInput.text.toString()
                profileModel.school = binding.myInfoSchoolInput.text.toString()
                profileModel.key = key

                galleryModel.galleryKey = key
                galleryModel.uid = currentUser?.uid
                galleryModel.artist = userModel?.name
                galleryModel.imagePath = path
                galleryModel.title = binding.myInfoTitleInput.text.toString()
                galleryModel.info = binding.myInfoInfoInput.text.toString()

                var map: MutableMap<String, Any> = HashMap()
                map.put("ifArtist", true)
                map.put("profileModel", profileModel)
                map.put("profileImgKey", key)
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