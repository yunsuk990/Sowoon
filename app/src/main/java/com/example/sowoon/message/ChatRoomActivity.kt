package com.example.sowoon.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.databinding.ActivityChatRoomBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatRoomBinding
    lateinit var firebaseDB: FirebaseDatabase
    private var chatRoomUid: String? = null
    private var destUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        var artist = intent.getStringExtra("Artist")
        destUid = intent.getStringExtra("userId") //채팅 상대방
        Log.d("destUid", destUid.toString())

        firebaseDB = FirebaseDatabase.getInstance()
        binding.chatRoomSendTitleTv.text = artist + "님에게 메세지"
        setContentView(binding.root)

        if(getJwt() != 0){
            binding.chatRoomSendBtn.setOnClickListener{ view ->
                var chatModel: ChatModel = ChatModel()
                chatModel.users?.put(destUid!!,true)
                chatModel.users?.put(getJwt().toString(), true)

                if(chatRoomUid == null){
                    binding.chatRoomSendBtn.isEnabled = false //요청 가기도 전에 버튼 누르는 것을 방지
                    firebaseDB.getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(object: OnSuccessListener<Void>{
                        override fun onSuccess(p0: Void?) {
                            checkChatRoom()
                        }
                    })
                }else{
                    var comment: ChatModel.Comment = ChatModel.Comment()
                    comment.userId = getJwt()
                    comment.message = binding.chatRoomSendEt.text.toString()
                    firebaseDB.getReference().child("chatrooms").child(chatRoomUid!!).child("comments").push().setValue(comment)
                }
            }
            checkChatRoom()
        }
    }


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
                            binding.chatRoomSendRv.adapter = ChatMessageRVAdapter(chatRoomUid!!)

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