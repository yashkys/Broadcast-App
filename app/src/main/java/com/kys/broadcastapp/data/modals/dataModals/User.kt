package com.kys.broadcastapp.data.modals.dataModals

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class User(
    @SerializedName(value = "userID") val userID: String= "",
    @SerializedName(value = "userName") var userName: String= "",
    @SerializedName(value = "email") var email: String= "",
    @SerializedName(value = "image") var image: String?= "",
    @SerializedName(value = "dob") var dob: String?= "",
): Serializable
