package com.kys.broadcastapp.data.modals.dataModals

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Message(
    @SerializedName(value = "message")  val message: String= "",
    @SerializedName(value = "messageID")  val messageID: String= "",
    @SerializedName(value = "messageTime")  val messageTime: String= "",
    @SerializedName(value = "messageType")  val messageType: String= "",
    @SerializedName(value = "senderID")  val senderID: String= "",
    @SerializedName(value = "isMessageRead")  val isMessageRead: Boolean= false
): Serializable
