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
    val REQ_GALLERY = 10
    var URI: Uri? = null
    lateinit var storage: FirebaseStorage

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

            if(bestartwork == null){
                binding.myInfoBestArtworkIv.setImageResource(R.drawable.add)
            }else{
                binding.myInfoBestArtworkIv.scaleType = (ImageView.ScaleType.FIT_XY)
                Glide.with(requireContext()).load(profile.bestArtwork).into(binding.myInfoBestArtworkIv)
            }
            binding.myInfoBestArtworkIv.setOnClickListener {
                //예시 삽입,, 나중에 앨범에서 이미지 가져오기
                startActivityForResult(Intent(requireContext(), AddGalleryActivity::class.java), 0)
            }
            binding.uploadBtn.setOnClickListener {
                uploadGallery(profile)
            }

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
//        binding.myInfoSchoolInput.text = user.school
//        binding.myInfoAwardsInput.text = user.awards
    }

    private fun uploadGallery(profile: Profile) {
        var mountainImageRef: StorageReference? = storage?.reference?.child("images")?.child(getJwt().toString())?.child("InfoGallery")?.child(URI?.lastPathSegment.toString())
        mountainImageRef?.putFile(URI!!)?.addOnSuccessListener {
            mountainImageRef.downloadUrl.addOnSuccessListener { url ->
                Log.d("FirebaseUri", url.toString())
                profile.bestArtwork = url.toString()
                database.profileDao().updateProfile(profile)
            }
        }?.addOnFailureListener{
            Toast.makeText(requireContext(), "업로드 실패", Toast.LENGTH_SHORT).show()
            Log.d("FirebaseUri", "FAIL", it)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode == AppCompatActivity.RESULT_OK){
//            when(requestCode){
//                REQ_GALLERY -> {
//                    data?.data?.let { uri ->
//                        URI = uri
//                        binding.myInfoBestArtworkIv.setImageURI(uri)
//                        binding.myInfoBestArtworkIv.scaleType = ImageView.ScaleType.FIT_XY
//                    }
//                }
//            }
//
//
//        }
//    }

    private fun getJwt(): Int? {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
                var GalleryId = data?.getStringExtra("GalleryId")
                Log.d("GalleryId", GalleryId.toString())
                Glide.with(requireContext()).load(GalleryId).into(binding.myInfoBestArtworkIv)

    }


}