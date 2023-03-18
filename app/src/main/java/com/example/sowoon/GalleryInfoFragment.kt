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
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.data.entity.User
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
import com.example.sowoon.message.ChatRoomActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

class GalleryInfoFragment : Fragment() {

    lateinit var binding: FragmentGalleryInfoBinding
    lateinit var database: AppDatabase
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    var galleryId: String = ""
    var user: User? = null
    private var gson = Gson()
    var galleryModel: GalleryModel? = null
    var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryInfoBinding.inflate(inflater, container, false)
        //database = AppDatabase.getInstance(requireContext())!!
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = Firebase.storage
        var ref = arguments?.getSerializable("gallery") as HomeFragment.reference
        var gallery = ref.gallery

        galleryModel = getFirebaseGallery(gallery)


        //if() binding.galleryInfoChatIv.visibility = View.INVISIBLE
//        setOption(gallery)
//        setGridView(gallery)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            currentUser = firebaseAuth.currentUser
        }
    }

    private fun getFirebaseGallery(gallery: StorageReference): GalleryModel {
        //var mountainImageRef: StorageReference? = firebaseStorage?.reference?.child("images")?.child(galleryPath)
        var ref = GalleryModel()
        gallery.metadata.addOnSuccessListener{ metadata ->
            Log.d("metadata", metadata.toString())
            ref.uid = metadata.getCustomMetadata("uid").toString()
            ref.title = metadata.getCustomMetadata("title").toString()
            ref.info = metadata.getCustomMetadata("info").toString()
            Log.d("metadata", metadata.getCustomMetadata("info").toString())
            setGallery(gallery)
            //GalleryModel.like = metadata.getCustomMetadata("like") as Int
        }
        return ref
    }

//    private fun setGridView(gallery: StorageReference) {
//        var datas = database.galleryDao().getUserGallery(getJwt()) as ArrayList<Gallery>
//        datas.remove(gallery)
//        var gridView = binding.galleryInfoGv
//        var adapter = ArtistGalleryGVAdapter(datas as ArrayList<Gallery>, requireContext())
//        adapter.itemClickListener(object: ArtistGalleryGVAdapter.MyItemClickListener{
//            override fun artworkClick(gallery: Gallery) {
//                ArtworkClick(gallery)
//            }
//        })
//        gridView.adapter = adapter
//
//    }

    private fun ArtworkClick(gallery: Gallery) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val galleryJson = gson.toJson(gallery)
                    putString("gallery", galleryJson)
                }
            })
            .commitNowAllowingStateLoss()
    }

    private fun setGallery(gallery: StorageReference){
        Glide.with(requireContext()).load(gallery.downloadUrl).into(binding.galleryInfoIv)
        gallery.downloadUrl.addOnSuccessListener{ uri ->
            var url = uri
            Glide.with(requireContext()).load(url).into(binding.galleryInfoIv)
        }
        binding.todayAlbumTitle.text = galleryModel?.title
        //binding.todayAlbumArtist.text = galleryModel?.artist
        binding.todayAlbumInfo.text = galleryModel?.info
        //binding.galleryInfoLikeCountTv.text = galleryModel?.like

        //좋아요 유무
//        if( currentUser != null){
//            if(gallery.favorites?.contains(getJwt()) == true){
//                binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
//            }else{
//                binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
//            }
//            initClickListener(gallery)
//        }

    }
//
//    private fun getJwt(): String {
//        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
//        var jwt = spf?.getString("jwt", null)
//        return jwt!!
//    }
//
//    private fun initClickListener(gallery: StorageReference?) {
//            var jwt = getJwt()
//            binding.galleryInfoHeartIv.setOnClickListener {
//                if(getJwt() != 0){
//                    var likeList: ArrayList<Int>? = gallery?.favorites
//                    var galleryLikeCount: Int = gallery?.like!!
//
//                    if(likeList?.contains(jwt) == true){
//                        likeList?.remove(jwt)
//                        galleryLikeCount -= 1
//                        binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
//                    }else{
//                        likeList?.add(jwt)
//                        galleryLikeCount += 1
//                        binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
//
//                    }
//                    Log.d("LikeList", likeList.toString())
//                    gallery.favorites = likeList
//                    gallery.like = galleryLikeCount
//                    database.galleryDao().updateGallery(gallery)
//                }else{
//                    Toast.makeText(context, "로그인 후 이용해주시길 바랍니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        binding.galleryInfoChatIv.setOnClickListener {
//            var intent = Intent(requireContext(), ChatRoomActivity::class.java)
//            intent.putExtra("userId", gallery?.userId.toString())
//            intent.putExtra("Artist", gallery?.artist.toString())
//            startActivity(intent)
//        }
//    }

    private fun setOption(gallery: StorageReference){
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

    private fun deleteGallery(gallery: StorageReference){
        //database.galleryDao().deleteGallery(galleryId)
        gallery.delete()
//        val desertRef = storage.reference.child("images")?.child(getJwt().toString())?.child(
//            galleryPath!!
//        )
//        desertRef?.delete()?.addOnSuccessListener {
//            Log.d("DELETE", "SUCCESS")
//            Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frame, GalleryFragment())
//                .commitNowAllowingStateLoss()
//        }?.addOnFailureListener{
//            Log.d("DELETE", "FAIL")
//            Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
//        }
    }



}