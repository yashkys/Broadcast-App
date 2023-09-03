package com.kys.broadcastapp.data.modals.dataModals

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ChatRoom(
    @SerializedName(value = "id")  val id : String= "",
    @SerializedName(value = "name")  var name : String= "",
    @SerializedName(value = "image")  var image : String= "",
    @SerializedName(value = "description")  var description : String= "",
    @SerializedName(value = "chatroomDescription")  var chatroomDescription : String= "",
    @SerializedName(value = "users")  var users: ArrayList<String> = arrayListOf(), /* list of user_id's*/
//    @SerializedName(value = "messages") var messages: ArrayList<String>?= arrayListOf(), /* list of message_id's*/
    @SerializedName(value = "lastMessage")  var lastMessage: Message?= Message()
): Serializable
