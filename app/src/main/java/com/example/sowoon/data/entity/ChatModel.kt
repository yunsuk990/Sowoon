package com.example.sowoon.data.entity

import java.util.Objects

class ChatModel {

    var users: MutableMap<String, Boolean>? = HashMap() //채팅방 유저들
    var comments: Map<String, Comment> = HashMap() //채팅방의 내용

    class Comment {
        var userId: Int? = 0
        var message: String? = ""
        var timestamp: Objects? = null
    }

}