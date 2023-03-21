package com.example.sowoon.data.entity

class ChatModel {

    var users: ArrayList<String> = ArrayList()
    var comments: MutableMap<String, Comment> = HashMap() //채팅방의 내용

    class Comment {
        var message: String? = null
        var userId: String? = null
        var timestamp: String? = null
    }

}