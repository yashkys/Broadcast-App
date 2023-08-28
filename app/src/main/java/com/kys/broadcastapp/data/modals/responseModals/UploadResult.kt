package com.kys.broadcastapp.data.modals.responseModals

sealed class UploadResult {
    data class InProgress(val progress: Int) : UploadResult()
    data class Success(val url: String) : UploadResult()
    data class Failure(val error: String) : UploadResult()
    object Resumed : UploadResult()
    object Paused : UploadResult()
    object Cancelled : UploadResult()
}
