package com.kys.broadcastapp.data.modals.dataModals

data class Message(
    val message: String,
    val messageID: String,
    val messageTime: String,
    val messageType: String,
    val senderID: String,
    val isMessageRead: Boolean
)
