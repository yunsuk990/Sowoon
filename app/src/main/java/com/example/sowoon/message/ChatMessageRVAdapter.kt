package com.example.sowoon.message

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sowoon.R
import com.example.sowoon.TodayGalleryRV
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ItemMessageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatMessageRVAdapter(): RecyclerView.Adapter<ChatMessageRVAdapter.ViewHolder>() {

    lateinit var comments: MutableList<ChatModel.Comment>
    lateinit var database: AppDatabase
    var jwt: Int = 0
    lateinit var Context: Context

    constructor(chatRoomUid: String, context: Context) : this() {
        Context = context
        database = AppDatabase.getInstance(context)!!
        comments = ArrayList()
        val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        jwt = spf?.getInt("jwt", 0)!!
        getMessageList(chatRoomUid)
    }

    interface uiInterface{
        fun scrollRV(size: Int)
    }

    private lateinit var mItemClickListener: ChatMessageRVAdapter.uiInterface

    fun setMyItemClickListener(itemClickListener: ChatMessageRVAdapter.uiInterface){
        mItemClickListener = itemClickListener
    }

    fun getMessageList(chatRoomUid: String){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                comments.clear()
                for(item in snapshot.children){
                    comments.add(item.getValue<ChatModel.Comment>()!!)
                    Log.d("Comment", comments.toString())
                }
                notifyDataSetChanged()
                mItemClickListener.scrollRV(comments.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    inner class ViewHolder(var binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatModel.Comment){
            var user = database.userDao().getUserProfile(item.userId!!)
            binding.messageItemTv.text = item.message.toString()
            binding.messageItemTimestamp.text = item.timestamp

            if(item.userId!!.equals(jwt)){
                binding.messageItemTv.setBackgroundResource(R.drawable.rightbubble)
                binding.messageItemLinear1.visibility = View.INVISIBLE
                binding.messageItemContainer.gravity = Gravity.RIGHT

            }else{
                Glide.with(Context).load(database.profileDao().getProfileImg(item.userId!!)).centerCrop().into(binding.messageItemIv)
                binding.messageItemTv.setBackgroundResource(R.drawable.leftbubble)
                binding.messageItemNameTv.text = user.name
                binding.messageItemLinear1.visibility = View.VISIBLE
                binding.messageItemContainer.gravity = Gravity.LEFT
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