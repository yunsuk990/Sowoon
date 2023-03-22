package com.example.sowoon.message

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityChatRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ChatRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatRoomBinding
    lateinit var firebaseDB: FirebaseDatabase
    private var chatRoomUid: String? = null
    private var destUid: String? = null
    lateinit var firebaseAuth: FirebaseAuth
    var time = SimpleDateFormat("HH:mm")
    var uid: String? = null


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
        binding.backBtn.setOnClickListener {
            finish()
            var intent = Intent(this, MessageMenu::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

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

                            var adapter = ChatMessageRVAdapter(chatRoomUid!!, baseContext, destUid!!)
                            adapter.setMyItemClickListener(object: ChatMessageRVAdapter.uiInterface{
                                override fun scrollRV(size: Int) {
                                    binding.chatRoomSendRv.scrollToPosition(size)
                                }
                            })
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
//                    notificationModel.to = userModel?.pushToken.toString()
                notificationModel.to = "cOOVJ76ARKq_SS_kDUhKEk:APA91bEuLKu9b9bIdvTFqJ_f51Scswo46sh3msYL57pOl-k6LIvPwcRY0qrDUfaM2Yqlmelw4iYcZM7lgKge6ARefsK-85RYCzLIOiC5rmTOog55AH-SKRjRtpTkh5Uxr0XrhQbgzv3M"
                notificationModel.notification.text = binding.chatRoomSendEt.text.toString()
                notificationModel.notification.title = "보낸이 아이디"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
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
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Okhttp3", "fail")
            }

        })
    }
}