package com.example.sowoon.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sowoon.R
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.databinding.ActivityChatRoomBinding
import com.example.sowoon.databinding.ItemMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatRoomBinding
    lateinit var firebaseDB: FirebaseDatabase
    private var chatRoomUid: String? = null
    private var destUid: String? = null
    lateinit var firebaseAuth: FirebaseAuth
    var time = SimpleDateFormat("HH:mm")
    var uid: String? = null
    var adapter: ChatMessageRVAdapter? = null
    var databaseReference: DatabaseReference? = null
    var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDB = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        uid = firebaseAuth.currentUser?.uid.toString()
        destUid = intent.getStringExtra("userUid").toString() //채팅을 당하는 유저 jwt
        var artist = intent.getStringExtra("artist").toString()
        Log.d("artist", artist)

        binding.chatRoomSendTitleTv.text = artist+ " 작가"

        //전송 버튼 클릭 시
        binding.chatRoomSendBtn.setOnClickListener {
            var chatModel = ChatModel()
            chatModel.users.add(destUid!!)
            chatModel.users.add(uid!!)

            if (chatRoomUid == null) {
                binding.chatRoomSendBtn.isEnabled = false //요청 가기도 전에 버튼 누르는 것을 방지
                firebaseDB.getReference().child("chatrooms").push().setValue(chatModel)
                    .addOnSuccessListener {
                        checkChatRoom()
                    }
            } else {
                var comment: ChatModel.Comment = ChatModel.Comment()
                comment.userId = uid
                comment.message = binding.chatRoomSendEt.text.toString()
                comment.timestamp = time.format(Date())
                firebaseDB.getReference().child("chatrooms").child(chatRoomUid!!).child("comments")
                    .push().setValue(comment).addOnCompleteListener {
                        sendFCM()
                        binding.chatRoomSendEt.setText("")
                    }
            }
        }
        checkChatRoom()
    }

    //유효성 검사
    private fun checkChatRoom(){
        firebaseDB.getReference().child("chatrooms").orderByChild("users/${uid}")
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for(item in snapshot.children){
                        //ChatModel.user 를 Map으로 정의해도 가져올떄 배열로 가져와서 오류
                        var chatModel: ChatModel = item.getValue<ChatModel>()!!

                        if(chatModel.users?.contains(destUid)!!){
                            chatRoomUid = item.key
                            binding.chatRoomSendBtn.isEnabled = true
                            binding.chatRoomSendRv.layoutManager = LinearLayoutManager(baseContext ,LinearLayoutManager.VERTICAL, false)
                            adapter = ChatMessageRVAdapter()
                            binding.chatRoomSendRv.adapter = adapter

                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendFCM(){
        var gson = Gson()
        var notificationModel: NotificationModel = NotificationModel()
        firebaseDB.getReference().child("users").child(destUid.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var userModel = snapshot.getValue(UserModel::class.java)
                notificationModel.to = userModel?.pushToken.toString()
                notificationModel.notification.text = binding.chatRoomSendEt.text.toString()
                notificationModel.notification.title = "보낸이 아이디"
                Log.d("destToken: ", notificationModel.to.toString())
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        var requestBody: RequestBody = RequestBody.create("application/json; charset=utf8".toMediaTypeOrNull(), gson.toJson(notificationModel))
        var request = Request.Builder()
            .header("Content-Type", "application/json")
            .addHeader("Authorization", "key=BFwG-SBccTjDIy9fDSldTjXwJtwHEcqW4tJcXiijgAdkaFaN4f7oTz_cHTnbo-TNr7edhDcrdidba74VKWLc09k")
            .url("https://fcm.googleapis.com/fcm/send")
            .post(requestBody)
            .build()

        var okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Okhttp3", "fail")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Okhttp3", response.message.toString())
            }

        })
    }


    inner class ChatMessageRVAdapter(): RecyclerView.Adapter<ChatMessageRVAdapter.ViewHolder>() {

        var comments: MutableList<ChatModel.Comment> = ArrayList()
        var userModel: UserModel? = null //상대방 유저 정보

        init {
            FirebaseDatabase.getInstance().reference.child("users").child(destUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userModel = snapshot.getValue(UserModel::class.java)
                    getMessageList()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        fun getMessageList(){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(
                chatRoomUid!!).child("comments")
            valueEventListener = databaseReference!!.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    var readUsersMap: MutableMap<String, Any> = HashMap()
                    for(item in snapshot.children){
                        var key: String = item.key.toString()
                        var comment: ChatModel.Comment = item.getValue<ChatModel.Comment>()!!
                        var comment2: ChatModel.Comment = item.getValue<ChatModel.Comment>()!!

                        comment2.readUsers.put(uid!!, true)
                        readUsersMap.put(key,comment2)
                        comments.add(comment)
                        Log.d("Comment", comments.toString())
                    }
                    if(comments.size != 0){
                        if(!(comments[comments.size-1].readUsers?.contains(uid))!!){
                            FirebaseDatabase.getInstance().reference.child("chatrooms").child(
                                chatRoomUid!!).child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener {
                                    notifyDataSetChanged()
                                    binding.chatRoomSendRv.scrollToPosition(comments.size - 1)
                                }
                        }else{
                            notifyDataSetChanged()
                            binding.chatRoomSendRv.scrollToPosition(comments.size - 1)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        inner class ViewHolder(var binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root){
            fun bind(item: ChatModel.Comment){
                var readUser = item.readUsers.size
                binding.messageItemTv.text = item.message.toString()
                binding.messageItemTimestamp.text = item.timestamp

                if((item.userId!!.equals(uid))){
                    //내가 보낸 메시지
                    binding.messageItemTv.setBackgroundResource(R.drawable.rightbubble)
                    binding.messageItemLinear1.visibility = View.INVISIBLE
                    binding.messageItemContainer.gravity = Gravity.RIGHT
                    binding.messageItemTimestamp.gravity = Gravity.RIGHT
                    if(readUser != 2){
                        binding.messageItemLeftReadUser.visibility = View.VISIBLE
                    }
                    else{
                        binding.messageItemLeftReadUser.visibility = View.INVISIBLE
                    }

                }else{
                    //상대가 보낸 메시지
                    Glide.with(this@ChatRoomActivity).load(userModel?.profileImg).centerCrop().into(binding.messageItemIv)
                    binding.messageItemTv.setBackgroundResource(R.drawable.leftbubble)
                    binding.messageItemNameTv.text = userModel?.name
                    binding.messageItemLinear1.visibility = View.VISIBLE
                    binding.messageItemContainer.gravity = Gravity.LEFT
                    binding.messageItemTimestamp.gravity = Gravity.LEFT
                    if(readUser != 2){
                        binding.messageItemRightReadUser.visibility = View.VISIBLE
                    }else{
                        binding.messageItemRightReadUser.visibility = View.INVISIBLE
                    }

                }

                Log.d("CommentText", item.message.toString())
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(comments[position])
        }

        override fun getItemCount(): Int = comments.size
    }

    override fun onBackPressed() {
        databaseReference?.removeEventListener(valueEventListener!!)
        finish()
        var intent = Intent(this, MessageMenu::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }


}