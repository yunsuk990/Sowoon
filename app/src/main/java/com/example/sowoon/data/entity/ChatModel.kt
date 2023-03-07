package com.example.sowoon.data.entity

import java.util.Objects

class ChatModel {

    var users: MutableMap<Int, Boolean>? = HashMap()
    var comments: Map<Int, Comment> = HashMap()

    class Comment {
        var userId: Int? = 0
        var message: String? = ""
        var timestamp: Objects? = null
    }

}