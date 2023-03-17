package com.example.sowoon.message

import com.google.firebase.messaging.RemoteMessage.Notification

class NotificationModel {

    var to: String? = null
    var notification: Notification = Notification()

    class Notification {
        var title: String? = null
        var text: String? = null
    }
}
