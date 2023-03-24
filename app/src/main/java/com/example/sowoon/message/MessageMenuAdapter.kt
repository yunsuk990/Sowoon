package com.example.sowoon.message

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.data.entity.UserModel
import com.example.sowoon.databinding.ItemMessageartistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class MessageMenuAdapter(context: Context): RecyclerView.Adapter<MessageMenuAdapter.ViewHolder>() {

    private var chatModel: ArrayList<ChatModel>? = ArrayList()
    var destinationId: String? = null
    var destinationUsers: ArrayList<String>? = ArrayList()
    var uid: String? = null
    var context = context
    var destName: String? = null

    init{
        uid = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/${uid}").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatModel?.clear()
                for(item in snapshot.children){
                    chatModel?.add(item.getValue(ChatModel::class.java)!!)
                }
                Log.d("initChatmodel ", chatModel.toString())
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    inner class ViewHolder(var binding: ItemMessageartistBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chat: ChatModel?){
            var desUser = chat?.users

            if (desUser != null) {
                for(users in desUser){
                    if(users != uid) destinationId = users
                    destinationUsers?.add(users)
                    Log.d("destinationId", destinationId.toString())
                }
            }

            FirebaseDatabase.getInstance().reference.child("users").child(destinationId.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var userModel = snapshot.getValue(UserModel::class.java)
                    Log.d("userModel", userModel.toString())
                    destName = userModel?.name
                    binding.messageartistTv.text = userModel?.name
                    Glide.with(context).load(userModel?.profileImg).centerCrop().into(binding.messageartistIv)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            var commentMap: MutableMap<String, ChatModel.Comment>? = TreeMap(Collections.reverseOrder())

            commentMap?.putAll(chat?.comments!!)
            var lastmessageKey: String? = commentMap?.keys?.toTypedArray()?.get(0)
            binding.messageartistMessage.text = chat?.comments?.get(lastmessageKey)?.message
            binding.messageartistTimestamp.text = chat?.comments?.get(lastmessageKey)?.timestamp
            binding.messageartistContainer.setOnClickListener{
                var intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra("userUid", destinationId)
                intent.putExtra("artist", destName)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = ItemMessageartistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chatModel?.get(position))

    }

    override fun getItemCount(): Int = chatModel!!.size

}