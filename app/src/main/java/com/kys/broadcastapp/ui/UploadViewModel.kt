package com.kys.broadcastapp.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kys.broadcastapp.data.modals.responseModals.UploadResult
import com.kys.broadcastapp.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var firebaseRepository: FirebaseRepository


    private val _uploadStatus = MutableLiveData<UploadResult>()
    val uploadStatus: LiveData<UploadResult> = _uploadStatus

    private val _uploadError = MutableLiveData<String>()
    val uploadError: LiveData<String> = _uploadError

    fun uploadMedia(mediaUri: Uri) {
        firebaseRepository.uploadFile(
            mediaUri,
            onProgress = { progress ->
                _uploadStatus.postValue(UploadResult.InProgress(progress))
            },
            onComplete = { url ->
                _uploadStatus.postValue(UploadResult.Success(url))
            },
            onFailure = { error ->
                _uploadStatus.postValue(UploadResult.Failure(error))
                _uploadError.postValue(error)
            },
            onCancel = {
                _uploadStatus.postValue(UploadResult.Cancelled)

            }
        )
    }
}
