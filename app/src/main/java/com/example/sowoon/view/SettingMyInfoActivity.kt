package com.example.sowoon.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.sowoon.MainActivity
import com.example.sowoon.R
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivitySettingMyInfoBinding
import com.example.sowoon.view.adapter.ArtistGalleryGVAdapter
import com.example.sowoon.viewmodel.AuthViewModel
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

class SettingMyInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingMyInfoBinding
    lateinit var gson: Gson
    lateinit var database: AppDatabase
    lateinit var storage: FirebaseStorage
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var authViewModel: AuthViewModel
    var currentUser: FirebaseUser? = null
    var userModel: UserModel? = null
    val REQ_ARTWORK = 11
    val REQ_GALLERY = 10
    var ProfileURI: Uri? = null
    var ArtworkURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storage = Firebase.storage
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        authViewModel = AuthViewModel()

        authViewModel.currentUserLiveData.observe(this, Observer {
            currentUser = it
            authViewModel.getUserProfile(it.uid)
        })

        authViewModel.userProfileLiveData.observe(this, Observer {
            userModel = it
            initInfo(it)
            setGridView(it)
        })
    }

    private fun initInfo(userModel: UserModel){
        binding.myInfoNameInput.setText(userModel!!.name)
        binding.myInfoAgeInput.setText(userModel!!.age)

        //프로필 이미지 초기화
        if(userModel!!.profileImg == null){
            binding.myInfoIv.setImageResource(R.drawable.add)
        }else{
            Glide.with(this).load(userModel!!.profileImg).into(binding.myInfoIv)
        }

        //화가일 경우
        if(userModel?.ifArtist == true){
            var profile = userModel!!.profileModel
            binding.myInfoBestArtworkContainer2.visibility = View.VISIBLE

            binding.myInfoSchool.visibility = View.VISIBLE
            binding.myInfoAwards.visibility = View.VISIBLE
            binding.myInfoSchoolInput.visibility = View.VISIBLE
            binding.myInfoAwardsInput.visibility = View.VISIBLE
            binding.myInfoSchoolInput.setText(profile?.school)
            binding.myInfoAwardsInput.setText(profile?.awards)
            getBestArwork(userModel!!.profileModel?.bestArtwork)
            binding.myInfoBestArtworkIv.setOnClickListener {
                openArtworkGallery()
            }

            //업로드 버튼 클릭 시
            binding.uploadBtn.setOnClickListener {
                uploadGallery(ProfileURI, ArtworkURI)
            }
            //일반 사용자 경우
        }else{
            binding.myInfoSchoolInput.visibility = View.INVISIBLE
            binding.myInfoSchool.visibility = View.INVISIBLE
            binding.myInfoAwardsInput.visibility = View.INVISIBLE
            binding.myInfoAwards.visibility = View.INVISIBLE

            //업로드 버튼 클릭 시
            binding.uploadBtn.setOnClickListener {
                uploadGallery(ProfileURI)
            }
        }

        //프로필 사진 수정
        binding.myInfoIv.setOnClickListener {
            openGallery()
        }

        binding.myInfoOptionIv.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                var popup: PopupMenu = PopupMenu(this@SettingMyInfoActivity, p0)
                MenuInflater(this@SettingMyInfoActivity).inflate(R.menu.option, popup.menu)
                popup.setOnMenuItemClickListener{ p0 ->
                    when(p0?.itemId){
//                        R.id.delete -> uploadGallery()
                        R.id.correct -> updateInfo()
                    }
                    true
                }
                popup.show()
            }
        })
    }


    fun setGridView(user: UserModel) {
        var gridView = binding.myInfoGalleryGridView
        var adapter = ArtistGalleryGVAdapter(this)
        adapter.itemClickListener(object: ArtistGalleryGVAdapter.MyItemClickListener{
            override fun artworkClick(gallery: GalleryModel) {
                ArtworkClick(gallery)
            }
        })
        gridView.adapter = adapter
        var arr = ArrayList<String>().add(user.jwt!!)
        firebaseDatabase.reference.child("images").orderByChild("likeUid").equalTo(arr).addListenerForSingleValueEvent(object:
            ValueEventListener {
            var galleryList = ArrayList<GalleryModel>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for( item in snapshot.children){
                    var galleryModel = item.getValue(GalleryModel::class.java)
                    if (galleryModel != null) {
                        galleryList.add(galleryModel)
                    }
                }
                adapter.addGalleryList(galleryList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun ArtworkClick(gallery: GalleryModel) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                var galleryJson = gson.toJson(gallery)
                var bundle = Bundle()
                bundle.putString("gallery", galleryJson)
                arguments = bundle
            })
            .commitNowAllowingStateLoss()
    }

    //수정하기 옵션
    private fun updateInfo() {
        if(userModel!!.ifArtist){
            binding.myInfoAgeInputCorrect.visibility = View.VISIBLE
            binding.myInfoAwardsInputCorrect.visibility = View.VISIBLE
            binding.myInfoNameInputCorrect.visibility = View.VISIBLE
            binding.myInfoNameSchoolCorrect.visibility = View.VISIBLE
        }else{
            binding.myInfoAgeInputCorrect.visibility = View.VISIBLE
            binding.myInfoNameInputCorrect.visibility = View.VISIBLE
        }
        binding.uploadBtn.visibility = View.VISIBLE

        binding.myInfoAgeInputCorrect.setOnClickListener {
            binding.myInfoAgeInput.isEnabled = true
        }
        binding.myInfoAwardsInputCorrect.setOnClickListener {
            binding.myInfoAwardsInput.isEnabled = true
        }
        binding.myInfoNameInputCorrect.setOnClickListener {
            binding.myInfoNameInput.isEnabled = true
        }
        binding.myInfoNameSchoolCorrect.setOnClickListener {
            binding.myInfoSchoolInput.isEnabled = true
        }
    }

    //프로필 사진 변경
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
            Glide.with(this).load(bestartwork).into(binding.myInfoBestArtworkIv)
        }
    }

    private fun uploadGallery(ProfileUri: Uri?, ArtworkUri: Uri?) {
        registProfileImage(ProfileUri, ArtworkUri )
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, SettingFragment())
            .commitNowAllowingStateLoss()
    }

    private fun uploadGallery(ProfileUri: Uri?) {
        registProfileImage(ProfileUri)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, SettingFragment())
            .commitNowAllowingStateLoss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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