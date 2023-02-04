package com.example.sowoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
    }

    private fun registInfo(){
        var jwt = getJwt()
        if(jwt == 0){
            Toast.makeText(this, "로그인 후 이용해주시기 바랍니다." ,Toast.LENGTH_SHORT).show()
            return
        }
        var school = binding.myInfoSchoolInput.text.toString()
        var awards = binding.myInfoAwardsInput.text.toString()
        //var bestArtwork = binding.myInfoBestArtworkIv
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


}