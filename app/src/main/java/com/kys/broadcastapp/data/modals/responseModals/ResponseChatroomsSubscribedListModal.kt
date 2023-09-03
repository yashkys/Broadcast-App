package com.kys.broadcastapp.data.modals.responseModals

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ResponseChatroomsSubscribedListModal (
    @SerializedName(value = "chatrooms")  var chatrooms:ArrayList<String>? = arrayListOf()
): Serializable