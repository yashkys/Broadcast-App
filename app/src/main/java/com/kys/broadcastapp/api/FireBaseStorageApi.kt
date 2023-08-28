package com.kys.broadcastapp.api

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kys.broadcastapp.data.modals.responseModals.UploadResult
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

fun StorageReference.uploadFile(file : Uri): LiveData<UploadResult> {
    val uploadLiveData = MutableLiveData<UploadResult>()
    val fileRef = file.lastPathSegment?.let { this.child(it) }
    val uploadTask= fileRef?.putFile(file)!!
    uploadTask.addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
        uploadLiveData.postValue(UploadResult.InProgress(progress))
    }

    uploadTask.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                val url = downloadUrl.toString()
                uploadLiveData.postValue(UploadResult.Success(url))
            }
        } else {
            val exception = task.exception
            val error = exception?.message ?: "Unknown error"
            uploadLiveData.postValue(UploadResult.Failure(error))
        }
    }
    return uploadLiveData
}
fun StorageReference.uploadFile(file : Uri, onComplete: (String)-> Unit): LiveData<UploadResult> {
    val uploadLiveData = MutableLiveData<UploadResult>()
    val fileRef = file.lastPathSegment?.let { this.child(it) }
    val uploadTask= fileRef?.putFile(file)!!
    uploadTask.addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
        uploadLiveData.postValue(UploadResult.InProgress(progress))
    }

    uploadTask.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                val url = downloadUrl.toString()
                uploadLiveData.postValue(UploadResult.Success(url))
                onComplete(url)
            }
        } else {
            val exception = task.exception
            val error = exception?.message ?: "Unknown error"
            uploadLiveData.postValue(UploadResult.Failure(error))
        }
    }
    return uploadLiveData
}
fun StorageReference.uploadByteArrayWithCallbacks(
    byteArray: ByteArrayOutputStream,
    onProgress: (Int) -> Unit,
    onComplete: (String) -> Unit,
    onFailure: (String) -> Unit,
    onCancel: () -> Unit
): LiveData<UploadResult> {
    val uploadLiveData = MutableLiveData<UploadResult>()
    // Generate a unique file name or path here if needed
    val fileRef = this.child("your_file_name.extension")

    val uploadTask = fileRef.putBytes(byteArray.toByteArray())

    uploadTask.addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
        uploadLiveData.postValue(UploadResult.InProgress(progress))
        onProgress(progress)
    }

    uploadTask.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                val url = downloadUrl.toString()
                uploadLiveData.postValue(UploadResult.Success(url))
                onComplete(url)
            }
        } else {
            val exception = task.exception
            val error = exception?.message ?: "Unknown error"
            uploadLiveData.postValue(UploadResult.Failure(error))
            onFailure(error)
        }
    }

    uploadTask.addOnCanceledListener {
        uploadLiveData.postValue(UploadResult.Cancelled)
        onCancel()
    }

    return uploadLiveData
}
fun StorageReference.uploadFileWithCallbacks(
    file: Uri,
    onProgress: (Int) -> Unit,
    onComplete: (String) -> Unit,
    onFailure: (String) -> Unit,
    onCancel: () -> Unit,
): LiveData<UploadResult> {
    val uploadLiveData = MutableLiveData<UploadResult>()
    val fileRef = file.lastPathSegment?.let { this.child(it) }
    val uploadTask = fileRef?.putFile(file)!!

    uploadTask.addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
        uploadLiveData.postValue(UploadResult.InProgress(progress))
        onProgress(progress)
    }

    uploadTask.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                val url = downloadUrl.toString()
                uploadLiveData.postValue(UploadResult.Success(url))
                onComplete(url)
            }
        } else {
            val exception = task.exception
            val error = exception?.message ?: "Unknown error"
            uploadLiveData.postValue(UploadResult.Failure(error))
            onFailure(error)
        }
    }

    uploadTask.addOnCanceledListener {
        uploadLiveData.postValue(UploadResult.Cancelled)
        onCancel()
    }



    return uploadLiveData
}
//fun StorageReference.getFileByUrl(url: String, filePath: String): LiveData<FileResult> {
//    val fileLiveData = MutableLiveData<FileResult>()
//    val httpReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
//    val file = File(filePath)
//    val getTask = httpReference.getFile(file)
//
//    getTask.addOnProgressListener { taskSnapshot ->
//        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
//        fileLiveData.postValue(FileResult.InProgress(progress = progress))
//    }
//    getTask.addOnSuccessListener {
//        fileLiveData.postValue(FileResult.Success(file))
//    }
//    getTask.addOnFailureListener { exception ->
//        val error = exception.message ?: "Unknown error"
//        fileLiveData.postValue(FileResult.Failure(error))
//    }
//
//    return fileLiveData
//}

