package com.kys.broadcastapp.utils

object FireStoreCollection{
    const val FRIEND_USER = "FRIEND_USER"
    const val CHATROOM_SUBSCRIBED ="CHATROOM_SUBSCRIBED"
    const val MESSAGE = "MESSAGE"
    const val CHATROOM_MESSAGES = "CHATROOM_MESSAGES"
    const val BROADCAST_CHANNEL = "BROADCAST_CHANNEL"
    const val CHATROOM = "CHATROOM"
    const val USER = "USER"
}
object FireStorageCollection{
    const val FILE = "FILE"
    const val IMAGE="IMAGE"
    const val VIDEO="VIDEO"
}
object FireStoreDocumentField {
    const val DOWNLOAD_URL = "download_url"
    const val DATE = "date"
    const val DOWNLOADS = "downloads"
    const val MESSAGE_TYPE_TEXT = "text"
    const val MESSAGE_TYPE_IMAGE = "image"
    const val MESSAGE_TYPE_VIDEO = "video"
    const val MESSAGE_TYPE_CALL = "call"
}
object Constants {
    const val FLAG_ACCEPT = 1
    const val FLAG_DENY = 0
    const val FLAG_ITEM_CLICK = 2
    const val FILE_SIZE = 100
}