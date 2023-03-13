package com.example.sowoon.message

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sowoon.R
import com.example.sowoon.data.entity.ChatModel
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.ActivityMessageMenuBinding
import com.example.sowoon.databinding.ItemMessageBinding
import com.example.sowoon.databinding.ItemMessageartistBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class MessageMenu : AppCompatActivity() {

    lateinit var binding: ActivityMessageMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var adapter = MessageMenuAdapter(getJwt()!!, baseContext)
        binding.messagemenuRv.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        binding.messagemenuRv.adapter = adapter


    }

    private fun getJwt(): Int? {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        var jwt = spf?.getInt("jwt", 0)
        return jwt
    }

    class MessageMenuAdapter(): RecyclerView.Adapter<MessageMenuAdapter.ViewHolder>() {

        private var chatModel: ArrayList<ChatModel> = ArrayList()
        private var Jwt: Int = 0
        private lateinit var database: AppDatabase
        var Context: Context? = null
        var destinationId: String? = null
        var destinationUsers: ArrayList<String> = ArrayList()

        constructor(jwt: Int, context: Context) : this() {
            Jwt = jwt
            Context = context
            database = AppDatabase.getInstance(context)!!
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+jwt).equalTo(true).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModel.clear()
                    for(item in snapshot.children){
                        chatModel.add(item.getValue<ChatModel>()!!)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }


        inner class ViewHolder(var binding: ItemMessageartistBinding): RecyclerView.ViewHolder(binding.root){
            fun bind(position: Int){
                var desUser = chatModel[position].users.keys

                for(users in desUser){
                    if(users != Jwt.toString()) destinationId = users
                    destinationUsers.add(users)
                }
                var profile = database.profileDao().getProfile(destinationId!!.toInt())
                binding.messageartistTv.text = profile?.name.toString()
                Glide.with(Context!!).load(profile?.profileImg).centerCrop().into(binding.messageartistIv)

                var commentMap: MutableMap<String,ChatModel.Comment> = TreeMap(Collections.reverseOrder())
                commentMap.putAll(chatModel[position].comments)
                var lastmessageKey = commentMap.keys.toTypedArray()[0]
                binding.messageartistMessage.text = chatModel[position].comments.get(lastmessageKey)?.message
                binding.messageartistTimestamp.text = chatModel[position].comments[lastmessageKey]?.timestamp

                binding.messageartistContainer.setOnClickListener{
                    var intent = Intent(Context, ChatRoomActivity::class.java)
                    intent.putExtra("userId", destinationUsers[position])
                    it.context.startActivity(intent)
                }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var binding = ItemMessageartistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position)

        }

        override fun getItemCount(): Int = chatModel.size

    }
}