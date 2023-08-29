package com.kys.broadcastapp.data.modals.dataModals

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ChatRoom(
    @SerializedName(value = "id")  val id : String= "",
    @SerializedName(value = "name")  val name : String= "",
    @SerializedName(value = "image")  val image : String= "",
    @SerializedName(value = "description")  val description : String= "",
    @SerializedName(value = "chatroomDescription")  val chatroomDescription : String= "",
    @SerializedName(value = "users")  val users: ArrayList<String> = arrayListOf(), /* list of user_id's*/
    @SerializedName(value = "messages")  val messages: ArrayList<String>?= arrayListOf(), /* list of message_id's*/
    @SerializedName(value = "lastMessage")  val lastMessage: Message?= Message()
): Serializable
