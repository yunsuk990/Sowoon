package com.example.sowoon.data.entity

import com.google.firebase.database.ServerValue
import java.util.Objects

class ChatModel {

    var users: MutableMap<String, Boolean> = HashMap() //채팅방 유저들
    var comments: MutableMap<String, Comment> = HashMap() //채팅방의 내용

    class Comment {
        var message: String? = null
        var userId: Int? = 0
        var timestamp: String? = null
    }

}