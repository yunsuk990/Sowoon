package com.example.sowoon.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sowoon.R
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.databinding.ActivityChatRoomBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatRoomBinding
    var getJwt = getJwt()
    lateinit var firebaseDB: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        var artist = intent.getStringExtra("Artist")
        var destUid = intent.getStringExtra("userId")
        var firebaseDB = Firebase.firestore
        binding.chatRoomSendTitleTv.text = artist + "님에게 메세지"
        setContentView(binding.root)

        if(getJwt != 0){
            binding.chatRoomSendBtn.setOnClickListener{ view ->
                var chatModel: ChatModel = ChatModel()
                chatModel.users?.put(getJwt!!, true)
                chatModel.users?.put(destUid?.toInt()!!, true)

                if(chatRoomUid == null){
                    Toast.makeText(this, "채팅방 생성", Toast.LENGTH_SHORT).show()
                    binding.chatRoomSendBtn.isEnabled = false
                    firebaseDB.collection("chatrooms").add(chatModel).addOnSuccessListener{
                        checkChatRoom()
                    }
                }else{
                    sendMsgDatabase()
                }
            }

        }

    }


    private fun checkChatRoom(){

    }

    private fun sendMsgDatabase(){
        if(!binding.chatRoomSendEt.text.toString().equals("")){
            
        }
    }

    private fun getJwt(): Int? {
        val spf = getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }
}