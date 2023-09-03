package com.kys.broadcastapp.data.modals.responseModals

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ResponseChatroomMessageListModal (
    @SerializedName(value = "messageIds")  var messageIds:ArrayList<String>? = arrayListOf()
): Serializable