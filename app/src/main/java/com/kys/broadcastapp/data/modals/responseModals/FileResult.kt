package com.kys.broadcastapp.data.modals.responseModals

import java.io.File

sealed class FileResult {
    data class InProgress(val progress: Int) : UploadResult()
    data class Success(val url: String) : UploadResult()
    data class Failure(val error: String) : UploadResult()
}

