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
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentSettingBinding
import com.example.sowoon.databinding.FragmentSettingMyInfoBinding
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
    var GalleryId: String = ""
    val REQ_GALLERY = 10
    var URI: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingMyInfoBinding.inflate(inflater, container, false)
        database = AppDatabase.getInstance(requireContext())!!
        storage = Firebase.storage
        var user: User = User()
        initInfo(user)
        return binding.root
    }

    private fun User(): User {
        gson = Gson()
        val spf =
            requireActivity().getSharedPreferences("userProfile", AppCompatActivity.MODE_PRIVATE)
        var user = spf.getString("user", null)
        return gson.fromJson(user, User::class.java)
    }

    private fun initInfo(user: User){
        var user = database.userDao().getUser(user.email, user.password)
        var profile: Profile? = null
        if(user?.ifArtist!!){
            profile = database.profileDao().getProfile(user.id)!!
            var bestartwork = profile?.bestArtwork
            binding.myInfoName.text = user.name
            binding.myInfoAgeInput.text = user.age
            binding.myInfoSchoolInput.text = profile?.school.toString()
            binding.myInfoAwardsInput.text = profile?.awards.toString()
            getProfileImage()
            getBestArwork(bestartwork)
            initClickListener(profile)
        }else{
            binding.myInfoName.text = user?.name
            binding.myInfoAgeInput.text = user?.age
            binding.myInfoSchoolInput.setOnClickListener {
                Toast.makeText(context, "화가 등록 후 설정하실 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
            binding.myInfoAwardsInput.setOnClickListener{
                Toast.makeText(context, "화가 등록 후 설정하실 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
            binding.myInfoBestArtworkIv.setOnClickListener {
                Toast.makeText(context, "화가 등록 후 설정하실 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBestArwork(bestartwork: String?) {
        if (bestartwork == null) {
            binding.myInfoBestArtworkIv.setImageResource(R.drawable.add)
        } else {
            binding.myInfoBestArtworkIv.scaleType = (ImageView.ScaleType.FIT_XY)
            var bestGallery = database.galleryDao().getBestArtwork(bestartwork)
            Glide.with(requireContext()).load(bestGallery?.GalleryId)
                .into(binding.myInfoBestArtworkIv)
        }
    }

    private fun getProfileImage() {
        var profileImg = database.profileDao().getProfileImg(getJwt()!!)
        if(profileImg != "") {
            binding.myInfoIv.scaleType = (ImageView.ScaleType.FIT_XY)
            Glide.with(requireContext()).load(profileImg).into(binding.myInfoIv)
        }else {
            binding.myInfoIv.scaleType = (ImageView.ScaleType.FIT_XY)
            binding.myInfoIv.setImageResource(R.drawable.add)
        }

    }

    private fun initClickListener(profile: Profile?) {
        //대표 이미지 가져오기
        binding.myInfoBestArtworkIv.setOnClickListener {
            startActivityForResult(Intent(requireContext(), AddGalleryActivity::class.java), 0)
        }
        //업로드 버튼 클릭 시
        binding.uploadBtn.setOnClickListener {
            uploadGallery(profile!!)
        }
        //프로필 사진 가져오기
        binding.myInfoIv.setOnClickListener {
            openGallery()
        }
    }

    private fun uploadGallery(profile: Profile) {
        if(GalleryId != ""){
            var gallery = database.galleryDao().getGallery(GalleryId)
            var galleryPath = gallery.galleryPath
            profile.bestArtwork = galleryPath
            database.profileDao().updateProfile(profile)
        }
        registProfileImage()
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, SettingFragment())
            .commitNowAllowingStateLoss()
    }

    private fun getJwt(): Int? {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    GalleryId = data?.getStringExtra("GalleryId")!!
                    Log.d("GalleryId", GalleryId.toString())
                    binding.myInfoBestArtworkIv.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(requireContext()).load(GalleryId).into(binding.myInfoBestArtworkIv)
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

    private fun registProfileImage(){
        var mountainImageRef: StorageReference? = storage?.reference?.child("images")?.child(getJwt().toString())?.child("Profile")?.child("profile.png")
        if(URI != null){
            mountainImageRef?.delete()?.addOnSuccessListener {
                mountainImageRef?.putFile(URI!!)?.addOnSuccessListener {
                    mountainImageRef.downloadUrl.addOnSuccessListener {
                        database.profileDao().updateProfileImg(getJwt()!!, it.toString())
                    }
                    Log.d("registProfileImage", "SUCCESS")
                }?.addOnFailureListener{
                    Log.d("registProfileImage", "FAIL", it)
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