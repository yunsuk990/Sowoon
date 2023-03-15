package com.example.sowoon.message

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityChatRoomBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
                            binding.chatRoomSendEt.setText("")
                        }
                }
            }
            checkChatRoom()

        }
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