package com.example.sowoon.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    lateinit var firebaseDB: FirebaseFirestore
    private var chatRoomUid: String? = null
    private var destUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        var artist = intent.getStringExtra("Artist")
        destUid = intent.getStringExtra("userId") //채팅 상대방
        Log.d("destUid", destUid.toString())

        var firebaseDB = Firebase.firestore
        binding.chatRoomSendTitleTv.text = artist + "님에게 메세지"
        setContentView(binding.root)

        if(getJwt() != 0){
            binding.chatRoomSendBtn.setOnClickListener{ view ->
                var chatModel: ChatModel = ChatModel()
                chatModel.users?.put(destUid!!,true)
                chatModel.users?.put(getJwt().toString(), true)

                if(chatRoomUid == null){
                    binding.chatRoomSendBtn.isEnabled = false
                    firebaseDB.collection("chatrooms").add(chatModel).addOnSuccessListener{
                        checkChatRoom()
                    }
                    Log.d("ChatRoom", "성공")
                }else{
                    Log.d("ChatRoom", "실패")
                    sendMsgDatabase()
                }
            }

        }

    }


    private fun checkChatRoom(){
//        firebaseDB.collection("chatrooms")
    }

    private fun sendMsgDatabase(){
        if(!binding.chatRoomSendEt.text.toString().equals("")){

        }
    }

    private fun getJwt(): Int? {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }
}