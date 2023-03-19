package com.example.sowoon

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.GalleryModel
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.data.entity.reference
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentGalleryInfoBinding
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
    lateinit var database: AppDatabase
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    var galleryId: String = ""
    private var gson = Gson()
    var currentUser: FirebaseUser? = null
    var userModel: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryInfoBinding.inflate(inflater, container, false)
        //database = AppDatabase.getInstance(requireContext())!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = Firebase.storage
        //var ref = arguments?.getSerializable("gallery") as reference
        var ref = arguments?.getSerializable("gallery") as reference
        var gallery = ref.gallery.getValue(GalleryModel::class.java)!!
        setGallery(gallery)

        Log.d("userModel", userModel.toString())
        if(currentUser?.uid == gallery.uid) {
            binding.galleryInfoChatIv.visibility = View.INVISIBLE
            binding.galleryOption.visibility = View.VISIBLE
            setOption(ref.gallery.key)
        }else{
            binding.galleryInfoChatIv.visibility = View.VISIBLE
            binding.galleryOption.visibility = View.INVISIBLE
        }
        setGridView(gallery, ref.gallery.key)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            currentUser = firebaseAuth.currentUser
            firebaseDatabase.reference.child("users").child(currentUser!!.uid).get().addOnSuccessListener { snapshot ->
                userModel = snapshot.getValue(UserModel::class.java)
            }
        }
    }

    private fun setGridView(gallery: GalleryModel, key: String?) {
        firebaseDatabase.getReference().child("images").orderByChild("artist").equalTo(gallery.artist).addListenerForSingleValueEvent(object: ValueEventListener{
            var galleryList = ArrayList<DataSnapshot>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for( item in snapshot.children){
                    if(item.key != key){
                        galleryList.add(item)
                    }
                }
                var gridView = binding.galleryInfoGv
                var adapter = ArtistGalleryGVAdapter(galleryList, requireContext())
                adapter.itemClickListener(object: ArtistGalleryGVAdapter.MyItemClickListener{
                    override fun artworkClick(gallery: DataSnapshot) {
                        ArtworkClick(gallery)
                    }
                })
                gridView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun ArtworkClick(gallery: DataSnapshot) {
        var ref = reference(gallery)
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, GalleryInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("gallery", ref)
//                    val galleryJson = gson.toJson(gallery)
//                    putString("gallery", galleryJson)
                }
            })
            .commitNowAllowingStateLoss()
    }

    private fun setGallery(gallery: GalleryModel){
        Glide.with(requireContext()).load(gallery.imagePath).into(binding.galleryInfoIv)
        binding.todayAlbumTitle.text = gallery?.title
        binding.todayAlbumArtist.text = gallery?.artist
        binding.todayAlbumInfo.text = gallery?.info
        binding.galleryInfoLikeCountTv.text = gallery?.like.toString()

        // 좋아요 유무
//        if(currentUser != null){
//            if(gallery.favorites?.contains(getJwt()) == true){
//                binding.galleryInfoHeartIv.setImageResource(R.drawable.fullheart)
//            }else{
//                binding.galleryInfoHeartIv.setImageResource(R.drawable.blankheart)
//            }
//            //initClickListener(gallery)
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