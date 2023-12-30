package com.example.sowoon.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sowoon.MainActivity
import com.example.sowoon.R
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
import com.example.sowoon.view.adapter.ArtistGalleryGVAdapter
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

class GalleryInfoFragment : Fragment() {

    lateinit var binding: FragmentGalleryInfoBinding
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    var gson: Gson = Gson()
    var currentUser: FirebaseUser? = null
    var gallery: GalleryModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryInfoBinding.inflate(inflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = Firebase.storage
        currentUser = firebaseAuth.currentUser

        binding.todayAlbumInfoCorrect.visibility = View.INVISIBLE
        binding.todayAlbumTitleCorrect.visibility = View.INVISIBLE
        binding.galleryButton.visibility = View.INVISIBLE
        binding.todayAlbumTitle.isEnabled = false
        binding.todayAlbumInfo.isEnabled = false

        //이미지 모델 가져오기
        var bundle = arguments
        gallery = gson.fromJson(bundle?.getString("gallery"), GalleryModel::class.java)

        Log.d("gallery", gallery?.galleryKey.toString())
        Log.d("gallery", gallery?.artist.toString())
        Log.d("gallery", gallery?.info.toString())
        //이미지 모델 정보 삽입하기
        setGallery(gallery)

        //작가 다른 작품
        setGridView(gallery)
        Log.d("gallery", gallery.toString())

        //대화방 이동
        binding.galleryInfoChatIv.setOnClickListener {
            var intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("userUid", gallery?.uid)
            intent.putExtra("artist", gallery?.artist.toString())
            startActivity(intent)
        }

        if(firebaseAuth.currentUser != null){
            Log.d("onStart CurrentUser", currentUser.toString())
            if(currentUser?.uid == gallery?.uid) {
                binding.galleryInfoChatIv.visibility = View.INVISIBLE
                binding.galleryOption.visibility = View.VISIBLE
                setOption(gallery!!.galleryKey)
            }else{
                binding.galleryInfoChatIv.visibility = View.VISIBLE
                binding.galleryOption.visibility = View.INVISIBLE
            }
        }
        return binding.root
    }
    @JvmName("setGallery1")
    private fun setGallery(gallery: GalleryModel?) {
        Glide.with(requireContext()).load(gallery?.imagePath).into(binding.galleryInfoIv)
        binding.todayAlbumTitle.setText(gallery?.title)
        binding.todayAlbumArtist.text = gallery?.artist
        binding.todayAlbumInfo.setText(gallery?.info)
        binding.galleryInfoLikeCountTv.text = gallery?.like.toString()

        // 좋아요 유무
        if(currentUser != null) {
            if(gallery?.likeUid?.contains(currentUser!!.uid) == true){
                binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
            }
            initClickListener(gallery?.galleryKey)
        }else{
            binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
            binding.galleryInfoHeartIv.setOnClickListener {
                Toast.makeText(requireContext(), "로그인 후 이용하시길 바랍니다." , Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setGridView(gallery: GalleryModel?) {
        var gridView = binding.galleryInfoGv
        var adapter = ArtistGalleryGVAdapter(requireContext())
        adapter.itemClickListener(object: ArtistGalleryGVAdapter.MyItemClickListener{
            override fun artworkClick(gallery: GalleryModel) {
                ArtworkClick(gallery)
            }
        })
        gridView.adapter = adapter
        firebaseDatabase.reference.child("images").orderByChild("artist").equalTo(gallery!!.artist).addListenerForSingleValueEvent(object: ValueEventListener{
            var galleryList = ArrayList<GalleryModel>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for( item in snapshot.children){
                    if(item.key != gallery?.galleryKey){
                        var galleryModel = item.getValue(GalleryModel::class.java)
                        if (galleryModel != null) {
                            galleryList.add(galleryModel)
                        }
                    }
                }
                adapter.addGalleryList(galleryList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun ArtworkClick(gallery: GalleryModel) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                var galleryJson = gson.toJson(gallery)
                var bundle = Bundle()
                bundle.putString("gallery", galleryJson)
                arguments = bundle
            })
            .commitNowAllowingStateLoss()
    }

    // 좋아요 버튼 클릭 시
    private fun initClickListener(key: String?) {
        var ref = firebaseDatabase.getReference().child("images").child(key!!)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var userList: ArrayList<String>? = ArrayList()
                var galleryModel: GalleryModel? = snapshot.getValue(GalleryModel::class.java)
                userList?.addAll(galleryModel?.likeUid!!)

                var galleryLikeCount = galleryModel!!.like
                var map = HashMap<String, Any>()

                binding.galleryInfoHeartIv.setOnClickListener {
                    if(userList!!.contains(currentUser!!.uid)){
                        galleryLikeCount -= 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
                        userList.remove(currentUser!!.uid)
                    }else{
                        galleryLikeCount += 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
                        userList.add(currentUser!!.uid)
                    }
                    map.put("likeUid", userList)
                    map.put("like", galleryLikeCount)
                    ref.updateChildren(map)
                    binding.galleryInfoLikeCountTv.text = galleryLikeCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setOption(key: String?){
        binding.galleryOption.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                var popup: PopupMenu = PopupMenu(context, p0)
                MenuInflater(context).inflate(R.menu.option, popup.menu)
                popup.setOnMenuItemClickListener { p0 ->
                    when (p0?.itemId) {
                        R.id.delete -> deleteGallery(key)
                        R.id.correct -> correctGallery(key)
                    }
                    true
                }
                popup.show()
            }

        })
    }

    private fun correctGallery(key: String?) {
        binding.galleryButton.visibility = View.VISIBLE
        binding.todayAlbumInfoCorrect.visibility = View.VISIBLE
        binding.todayAlbumTitleCorrect.visibility = View.VISIBLE
        binding.todayAlbumInfoCorrect.setOnClickListener {
            binding.todayAlbumInfo.isEnabled = true
        }
        binding.todayAlbumTitleCorrect.setOnClickListener {
            binding.todayAlbumTitle.isEnabled = true
        }

        binding.galleryButton.setOnClickListener{
            var info = binding.todayAlbumInfo.text.toString()
            var title = binding.todayAlbumTitle.text.toString()
            var map: MutableMap<String, Any> = HashMap()
            map.put("info", info)
            map.put("title", title)
            firebaseDatabase.reference.child("images").child(key!!).updateChildren(map).addOnCompleteListener {
                binding.todayAlbumInfoCorrect.visibility = View.INVISIBLE
                binding.todayAlbumTitleCorrect.visibility = View.INVISIBLE
                binding.todayAlbumTitle.isEnabled = false
                binding.todayAlbumInfo.isEnabled = false
                binding.galleryButton.visibility = View.INVISIBLE
            }

        }

    }

    private fun deleteGallery(key: String?){
        var mountainImageRef: StorageReference? = firebaseStorage?.reference?.child("images")?.child(key!!)
        mountainImageRef?.delete()?.addOnSuccessListener {
            Log.d("DELETE", "SUCCESS")
            firebaseDatabase.reference.child("images")?.child(key!!)?.removeValue()
            Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, GalleryFragment())
                .commitNowAllowingStateLoss()
        }?.addOnFailureListener{
            Log.d("DELETE", "FAIL")
            Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
        }
    }
}