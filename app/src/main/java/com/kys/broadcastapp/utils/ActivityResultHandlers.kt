package com.kys.broadcastapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.kys.broadcastapp.ui.UploadViewModel

/*
class ActivityResultHandlers(private val activity: Activity) {

    @Inject
    lateinit var firebaseRepository: FirebaseRepository

    private val fragment = activity as? Fragment

    val captureImageLauncher = fragment?.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result here for image capture
            val data: Intent? = result.data
            // Process the captured image data
            val bitmap = data!!.extras!!["data"] as Bitmap?

            val bytes = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

            bytes?.let {
                firebaseRepository.saveByteArrayWithCallbacks(it, {}, {}, {}
            ) {}
            }
        }
    } ?: throw IllegalArgumentException("Activity must be attached to a fragment")

    val pickImageLauncher = fragment?.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result here for picking an image from the gallery
            val data: Intent? = result.data
            // Process the picked image data
            val uri = data!!.data
            uri?.let { firebaseRepository.saveFile(it) }
        }
    } ?: throw IllegalArgumentException("Activity must be attached to a fragment")

    val pickVideoLauncher = fragment?.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result here for picking a video from the gallery
            val data: Intent? = result.data
            // Process the picked video data

            val uri = data!!.data
            uri?.let { firebaseRepository.saveFile(it) }
        }
    } ?: throw IllegalArgumentException("Activity must be attached to a fragment")
}
*/
class ActivityResultHandlers(private val fragment: Fragment, private val viewModel: UploadViewModel) {

    val pickImageLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.data
                if (data != null) {
                    viewModel.uploadMedia(data)
                }
            }
        }

    val captureImageLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoUri = result.data?.extras?.getParcelable<Uri>("data")
                if (photoUri != null) {
                    viewModel.uploadMedia(photoUri)
                }
            }
        }

    val pickVideoLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.data
                if (data != null) {
                    viewModel.uploadMedia(data)
                }
            }
        }

    val pickDocumentLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.data
                if (data != null) {
                    viewModel.uploadMedia(data)
                }
            }
        }


    // Add more launchers for capturing/picking other types of documents if needed
}
