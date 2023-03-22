package com.example.sowoon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
import com.example.sowoon.message.ChatRoomActivity
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

        //이미지 모델 가져오기
        var bundle = arguments
        var galleryGson = bundle?.getString("gallery")
        gallery = gson.fromJson(galleryGson, GalleryModel::class.java)

        //이미지 모델 정보 삽입하기
        setGallery(gallery!!)

        //작가 다른 작품
        setGridView(gallery!!)
        Log.d("gallery", gallery.toString())

        //대화방 이동
        binding.galleryInfoChatIv.setOnClickListener {
            var intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("userUid", gallery?.uid)
            intent.putExtra("Artist", gallery?.artist.toString())
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
    private fun setGallery(gallery: GalleryModel) {
        Glide.with(requireContext()).load(gallery!!.imagePath).into(binding.galleryInfoIv)
        binding.todayAlbumTitle.text = gallery?.title
        binding.todayAlbumArtist.text = gallery?.artist
        binding.todayAlbumInfo.text = gallery?.info
        binding.galleryInfoLikeCountTv.text = gallery?.like.toString()

        // 좋아요 유무
        if(currentUser != null) {
            if(gallery!!.likeUid.contains(currentUser!!.uid)){
                binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
            }
            initClickListener(gallery!!.galleryKey)
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
        firebaseDatabase.getReference().child("images").orderByChild("artist").equalTo(gallery!!.artist).addListenerForSingleValueEvent(object: ValueEventListener{
            var galleryList = ArrayList<GalleryModel>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for( item in snapshot.children){
                    if(item.key != gallery!!.galleryKey){
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
                bundle.putString("artist", gallery.artist)
                arguments = bundle
            })
            .commitNowAllowingStateLoss()
    }

    // 좋아요 버튼 클릭 시
    private fun initClickListener(key: String?) {
        var ref = firebaseDatabase.getReference().child("images").child(key!!)
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var userList: ArrayList<String>? = ArrayList()
                var galleryModel: GalleryModel? = null
                    galleryModel = snapshot.getValue(GalleryModel::class.java)
                    userList?.addAll(galleryModel!!.likeUid)

                var galleryLikeCount = galleryModel!!.like

                binding.galleryInfoHeartIv.setOnClickListener {
                    if(userList!!.contains(currentUser!!.uid)){
                        galleryLikeCount -= 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)

                        var map = HashMap<String, Any>()
                        userList.remove(currentUser!!.uid)
                        map.put("likeUid", userList)
                        ref.updateChildren(map)
                    }else{
                        galleryLikeCount += 1
                        binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)

                        var map = HashMap<String, Any>()
                        userList.add(currentUser!!.uid)
                        map.put("likeUid", userList)
                        map.put("like", galleryLikeCount)
                        ref.updateChildren(map)
                    }
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
                        R.id.correct -> Toast.makeText(context, "수정 클릭", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                popup.show()
            }

        })
    }

    private fun deleteGallery(key: String?){
        var mountainImageRef: StorageReference? = firebaseStorage?.reference?.child("images")?.child(key!!)
        mountainImageRef?.delete()?.addOnSuccessListener {
            Log.d("DELETE", "SUCCESS")
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