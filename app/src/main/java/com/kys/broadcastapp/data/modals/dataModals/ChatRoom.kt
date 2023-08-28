package com.kys.broadcastapp.data.modals.dataModals

import java.io.Serializable

data class ChatRoom(
    val id : String,
    val name : String,
    val image : String,
    val description : String,
    val chatroomDescription : String,
    val users: ArrayList<String>, /* list of user_id's*/
    val messages: ArrayList<String>?, /* list of message_id's*/
    val lastMessage: Message?
)
