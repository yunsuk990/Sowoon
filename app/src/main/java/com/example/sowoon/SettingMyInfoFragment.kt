package com.example.sowoon

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.disklrucache.DiskLruCache.Value
import com.example.sowoon.data.entity.*
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentSettingBinding
import com.example.sowoon.databinding.FragmentSettingMyInfoBinding
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
import kotlin.math.exp

class SettingMyInfoFragment : Fragment() {

    lateinit var binding: FragmentSettingMyInfoBinding
    lateinit var gson: Gson
    lateinit var database: AppDatabase
    lateinit var storage: FirebaseStorage
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth
    val REQ_ARTWORK = 11
    val REQ_GALLERY = 10
    var ProfileURI: Uri? = null
    var ArtworkURI: Uri? = null
    var currentUser: FirebaseUser? = null
    var userModel: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingMyInfoBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        storage = Firebase.storage
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        currentUser = firebaseAuth.currentUser
        initInfo()
        return binding.root
    }

    private fun initInfo(){
        firebaseDatabase.reference.child("users").child(currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userModel = snapshot.getValue(UserModel::class.java)
                binding.myInfoName.text = userModel!!.name
                binding.myInfoAgeInput.text = userModel!!.age

                //프로필 이미지 초기화
                if(userModel!!.profileImg == null){
//            binding.myInfoIv.scaleType = (ImageView.ScaleType.FIT_XY)
                    binding.myInfoIv.setImageResource(R.drawable.add)
                }else{
//            binding.myInfoIv.scaleType = (ImageView.ScaleType.FIT_XY)
                    Glide.with(requireContext()).load(userModel!!.profileImg).into(binding.myInfoIv)
                }

                //프로필 사진 수정
                binding.myInfoIv.setOnClickListener {
                    openGallery()
                }

                if(userModel?.ifArtist == true){
                    var profile = userModel!!.profileModel
                    binding.myInfoSchoolInput.text = profile?.school
                    binding.myInfoAwardsInput.text = profile?.awards
                    getBestArwork(userModel!!.profileModel?.bestArtwork)

                    binding.myInfoBestArtworkIv.setOnClickListener {
                        openArtworkGallery()
                    }

                    //업로드 버튼 클릭 시
                    binding.uploadBtn.setOnClickListener {
                        uploadGallery(ProfileURI, ArtworkURI)
                    }

                }else{
                    binding.myInfoSchoolInput.visibility = View.INVISIBLE
                    binding.myInfoAwardsInput.visibility = View.INVISIBLE
                    binding.myInfoBestArtworkIv.visibility = View.INVISIBLE
                    //업로드 버튼 클릭 시
                    binding.uploadBtn.setOnClickListener {
                        uploadGallery(ProfileURI)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun openArtworkGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_ARTWORK)
    }

    private fun getBestArwork(bestartwork: String?) {
        if (bestartwork == null) {
            binding.myInfoBestArtworkIv.setImageResource(R.drawable.add)
        } else {
            binding.myInfoBestArtworkIv.scaleType = (ImageView.ScaleType.FIT_XY)
            Glide.with(requireContext()).load(bestartwork).into(binding.myInfoBestArtworkIv)
        }
    }

    private fun uploadGallery(ProfileUri: Uri?) {
        registProfileImage(ProfileUri)
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, SettingFragment())
            .commitNowAllowingStateLoss()
    }

    private fun uploadGallery(ProfileUri: Uri?, ArtworkUri: Uri?) {
        registProfileImage(ProfileUri, ArtworkUri )
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, SettingFragment())
            .commitNowAllowingStateLoss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                //프로필
                REQ_GALLERY -> {
                    data?.data?.let { uri ->
                        ProfileURI = uri
                        binding.myInfoIv.setImageURI(uri)
                        binding.myInfoIv.scaleType = ImageView.ScaleType.FIT_XY
                    }
                }

                // 대표작
                REQ_ARTWORK -> {
                    data?.data?.let { uri ->
                        ArtworkURI = uri
                        binding.myInfoBestArtworkIv.setImageURI(uri)
                        binding.myInfoBestArtworkIv.scaleType = ImageView.ScaleType.FIT_XY
                    }
                }

            }
        }
    }

    private fun registProfileImage(ProfileUri: Uri?) {
        var mountainImageRef: StorageReference? = storage?.reference?.child("images")
        var path = ProfileUri?.lastPathSegment.toString()

        //원래 사진 삭제
        mountainImageRef?.child(userModel!!.profieImgKey!!)?.delete()
        ProfileUri?.let {
            mountainImageRef?.child(path)?.putFile(it)?.addOnCompleteListener {
                mountainImageRef.downloadUrl.addOnSuccessListener { uri ->
                    var map: MutableMap<String, Any> = HashMap()
                    map.put("profieImgKey", path)
                    map.put("profileImg", uri.toString())
                    map.put("name", binding.myInfoName.text.toString())
                    map.put("age", binding.myInfoAgeInput.text.toString())
                    firebaseDatabase.reference.child("users").child(currentUser!!.uid)
                        .updateChildren(map)
                }
            }
        }
    }

    private fun registProfileImage(ProfileUri: Uri?, ArtworkUri: Uri?) {
        var mountainImageRef: StorageReference? = storage?.reference?.child("images")
        var path1 = ProfileUri?.lastPathSegment.toString()
        var path2 = ArtworkUri?.lastPathSegment.toString()

        //원래 사진 삭제
        mountainImageRef?.child(userModel!!.profieImgKey!!)?.delete()
        mountainImageRef?.child(userModel!!.profileModel?.key!!)?.delete()

        //프로필 업로드
        ProfileUri?.let {
            mountainImageRef?.child(path1)?.putFile(it)?.addOnCompleteListener {
                mountainImageRef.downloadUrl.addOnSuccessListener { uri ->
                    var map: MutableMap<String, Any> = HashMap()
                    map.put("profieImgKey", path1)
                    map.put("profileImg", uri.toString())
                    map.put("name", binding.myInfoName.text.toString())
                    map.put("age", binding.myInfoAgeInput.text.toString())
                    map.put("school", binding.myInfoSchoolInput.text.toString())
                    map.put("awards", binding.myInfoAwardsInput.text.toString())
                    firebaseDatabase.reference.child("users").child(currentUser!!.uid)
                        .updateChildren(map)
                }
            }
        }

        //대표작 업로드
        ArtworkUri?.let{
            mountainImageRef?.child(path2)?.putFile(it)?.addOnCompleteListener {
                mountainImageRef.downloadUrl.addOnSuccessListener { uri ->
                    var map: MutableMap<String, Any> = HashMap()
                    map.put("profileModel/bestArtwork", uri)
                    map.put("profileModel/key", path2)
                    firebaseDatabase.reference.child("users").child(currentUser!!.uid)
                        .updateChildren(map)
                }
            }

        }


    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_GALLERY)
    }
}