package com.kys.broadcastapp.data.modals.dataModals

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ResponseFriendListDataModal (
    @SerializedName(value = "friends")  var friends:List<String>? = listOf()
): Serializable