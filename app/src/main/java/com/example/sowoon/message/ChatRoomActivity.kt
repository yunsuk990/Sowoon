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
        destUid = intent.getStringExtra("userId") //채팅 상대방
        var artist = database.userDao().getUserProfile(destUid!!.toInt()).name
        firebaseDB = FirebaseDatabase.getInstance()
        binding.chatRoomSendTitleTv.text = artist+ " 작가"
        binding.backBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, MessageMenu::class.java))
        }

        if(getJwt() != 0){
            chatRoomSend()
            checkChatRoom()
        }
    }

    //메세지 보내기
    private fun chatRoomSend() {
        binding.chatRoomSendBtn.setOnClickListener { view ->
            var chatModel: ChatModel = ChatModel()
            chatModel.users?.put(destUid!!, true)
            chatModel.users?.put(getJwt().toString(), true)

            if (chatRoomUid == null) {
                binding.chatRoomSendBtn.isEnabled = false //요청 가기도 전에 버튼 누르는 것을 방지
                firebaseDB.getReference().child("chatrooms").push().setValue(chatModel)
                    .addOnSuccessListener(object : OnSuccessListener<Void> {
                        override fun onSuccess(p0: Void?) {
                            checkChatRoom()
                        }
                    })
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
    }


    //유효성 검사
    private fun checkChatRoom(){
        Log.d("checkChatRoom", "Start")
        firebaseDB.getReference().child("chatrooms").orderByChild("users/${getJwt()}").equalTo(true)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(item in snapshot.children){
                        var chatModel: ChatModel? = item.getValue<ChatModel>()
                        if(chatModel?.users?.contains(destUid)!!){
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