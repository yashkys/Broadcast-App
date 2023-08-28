package com.kys.broadcastapp.data.modals.responseModals

import org.json.JSONObject

data class BasicResponseModal(
    val message:String="",
    val output:List<JSONObject>,
    val status:String?,
)

data class CustomResponseModal<T>(
    val message:String="",
    val output:T,
    val status:String?,
)

data class CustomListResponseModal<T>(
    val message:String="",
    val output:List<T>,
    val status:String?,
)