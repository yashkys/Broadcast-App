package com.kys.broadcastapp.data.modals.dataModals

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ResponseBroadcastChannelListModal (
    @SerializedName(value = "broadcastChannelsID")  var chatroomIds:ArrayList<String>? = arrayListOf()
): Serializable