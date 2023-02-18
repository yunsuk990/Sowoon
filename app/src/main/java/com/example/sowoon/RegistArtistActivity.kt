package com.example.sowoon

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.example.sowoon.data.entity.Profile
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityRegistArtistBinding
import com.google.gson.Gson

class RegistArtistActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistArtistBinding
    lateinit var database: AppDatabase
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getInstance(this)!!
        binding.myInfoName.text = getName()
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

        //firebase에 사진 저장하기

        var profile: Profile = Profile(school,awards,getName(), null, getJwt()!!)
        database.profileDao().insertProfile(profile)
        database.userDao().ifArtistRegist(jwt!!,true)
        Toast.makeText(this, "등록되었습니다." ,Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun User(): User? {
        gson = Gson()
        val spf = getSharedPreferences("userProfile", MODE_PRIVATE)
        return gson.fromJson(spf.getString("user", null), User::class.java)
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
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                var uri = data?.getStringExtra("Uri")?.toUri()
                binding.myInfoBestArtworkIv.setImageURI(uri)

            }
        }
    }
}