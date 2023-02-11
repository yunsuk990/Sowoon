package com.example.sowoon

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.Gallery
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

class GalleryInfoFragment : Fragment() {

    lateinit var binding: FragmentGalleryInfoBinding
    lateinit var database: AppDatabase
    lateinit var storage: FirebaseStorage
    var galleryId: String = ""
    var user: User? = null
    private var gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryInfoBinding.inflate(inflater, container, false)
        Log.d("database", "database")
        database = AppDatabase.getInstance(requireContext())!!
        storage = Firebase.storage
        user = User()
        val galleryJson = arguments?.getString("gallery")
        val gallery = gson.fromJson(galleryJson, Gallery::class.java)
        galleryId = gallery.GalleryId
        setOption(gallery)
        setGallery(gallery)
        return binding.root
    }

    private fun setGallery(gallery: Gallery){
        Glide.with(requireContext()).load(gallery.GalleryId).into(binding.galleryInfoIv)
        binding.todayAlbumTitle.text = gallery.title
        binding.todayAlbumArtist.text = gallery.artist
        binding.todayAlbumInfo.text = gallery.info
        binding.galleryInfoLikeCountTv.text = gallery.like.toString()
        if(user != null){
            if(gallery.favorites?.contains(getJwt()) == true){
                binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
            }else{
                binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
            }
            initClickListener(gallery)
        }

    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt!!
    }

    private fun initClickListener(gallery: Gallery?) {
            var jwt = getJwt()
            binding.galleryInfoHeartIv.setOnClickListener {
                if(getJwt() != 0){
                    var likeList: ArrayList<Int>? = gallery?.favorites
                    if(likeList == null) likeList = ArrayList()
                    var galleryLikeCount: Int = gallery?.like!!

                    if(likeList?.contains(jwt) == true){
                        likeList?.remove(jwt)
                        galleryLikeCount -= 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
                    }else{
                        likeList?.add(jwt)
                        galleryLikeCount += 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)

                    }
                    Log.d("LikeList", likeList.toString())
                    gallery.favorites = likeList
                    gallery.like = galleryLikeCount
                    database.galleryDao().updateGallery(gallery)
                }else{
                    Toast.makeText(context, "로그인 후 이용해주시길 바랍니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun User(): User? {
        gson = Gson()
        val spf =
            requireActivity().getSharedPreferences("userProfile", AppCompatActivity.MODE_PRIVATE)
        return gson.fromJson(spf.getString("user", null), User::class.java)
    }

    private fun setOption(gallery: Gallery){
        binding.galleryOption.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                var popup: PopupMenu = PopupMenu(context, p0)
                MenuInflater(context).inflate(R.menu.option, popup.menu)
                popup.setOnMenuItemClickListener { p0 ->
                    when (p0?.itemId) {
                        R.id.delete -> deleteGallery(gallery)
                        R.id.correct -> Toast.makeText(context, "수정 클릭", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                popup.show()
            }

        })
    }

    private fun deleteGallery(gallery: Gallery?){
        database.galleryDao().deleteGallery(galleryId)
        var galleryPath = gallery?.galleryPath
        val desertRef = storage.reference.child("images/"+ getJwt()+galleryPath)
        desertRef.delete().addOnSuccessListener {
            Log.d("DELETE", "SUCCESS")
            Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Log.d("DELETE", "FAIL")
            Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
        }
    }


}