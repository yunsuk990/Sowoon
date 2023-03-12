package com.example.sowoon.message

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.databinding.ItemMessageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatMessageRVAdapter(): RecyclerView.Adapter<ChatMessageRVAdapter.ViewHolder>() {

    lateinit var comments: MutableList<ChatModel.Comment>

    constructor(chatRoomUid: String) : this() {
        comments = ArrayList()
        Log.d("chatRoomUid", chatRoomUid.toString())
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                comments.clear()
                for(item in snapshot.children){
                    comments.add(item.getValue<ChatModel.Comment>()!!)
                    Log.d("Comment", comments.toString())
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    inner class ViewHolder(var binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatModel.Comment){
            binding.messageItemTv.text = item.message.toString()
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