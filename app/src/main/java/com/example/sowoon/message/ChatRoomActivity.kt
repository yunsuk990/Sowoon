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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
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
    lateinit var database: AppDatabase
    var time = SimpleDateFormat("HH:mm")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = AppDatabase.getInstance(this)!!
        destUid = intent.getStringExtra("userId").toString() //채팅을 당하는 유저 jwt
        var artist = database.userDao().getUserProfile(destUid!!.toInt()).name
        firebaseDB = FirebaseDatabase.getInstance()
        binding.chatRoomSendTitleTv.text = artist+ " 작가"
        binding.backBtn.setOnClickListener {
            finish()
            var intent = Intent(this, MessageMenu::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        if(getJwt() != 0){
            binding.chatRoomSendBtn.setOnClickListener {
                var chatModel = ChatModel()
                chatModel.users.add(destUid.toString())
                chatModel.users.add(getJwt().toString())
                Log.d("user", chatModel.users.toString())

                if (chatRoomUid == null) {
                    binding.chatRoomSendBtn.isEnabled = false //요청 가기도 전에 버튼 누르는 것을 방지
                    firebaseDB.getReference().child("chatrooms").push().setValue(chatModel)
                        .addOnSuccessListener {
                            checkChatRoom()
                        }
                } else {
                    var comment: ChatModel.Comment = ChatModel.Comment()
                    comment.userId = getJwt()
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
    }

    private fun sendFCM(){
        var gson = Gson()
        var notificationModel: NotificationModel = NotificationModel()
        firebaseDB.getReference().child("users").child(destUid!!).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children){
                    var userModel = item.getValue(UserModel::class.java)
                    notificationModel.to = userModel?.pushToken.toString()
                    notificationModel.notification.text = binding.chatRoomSendEt.text.toString()
                    notificationModel.notification.title = "보낸이 아이디"
                }
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
            }

        })
    }

    //유효성 검사
    private fun checkChatRoom(){
        Log.d("checkChatRoom", "Start")
        firebaseDB.getReference().child("chatrooms").orderByChild("users/${getJwt()}")
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("item", snapshot.children.toString())
                    for(item in snapshot.children){
                        Log.d("desUid", destUid.toString())


                        //ChatModel.user 를 Map으로 정의해도 가져올떄 배열로 가져와서 오류
                        var chatModel: ChatModel = item.getValue<ChatModel>()!!
                        Log.d("array", chatModel.users.toString())


                        if(chatModel.users?.contains(destUid)!!){
                            chatRoomUid = item.key
                            binding.chatRoomSendBtn.isEnabled = true

                            binding.chatRoomSendRv.layoutManager = LinearLayoutManager(baseContext ,LinearLayoutManager.VERTICAL, false)
                            var adapter = ChatMessageRVAdapter(chatRoomUid!!, baseContext)
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

    private fun getJwt(): Int? {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }

}