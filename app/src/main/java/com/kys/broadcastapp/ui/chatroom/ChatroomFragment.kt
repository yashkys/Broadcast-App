package com.kys.broadcastapp.ui.chatroom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FieldValue
import com.kys.broadcastapp.R
import com.kys.broadcastapp.adapter.MessageAdapter
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.data.modals.dataModals.Message
import com.kys.broadcastapp.data.modals.responseModals.UploadResult
import com.kys.broadcastapp.databinding.ChatFileOptionsBinding
import com.kys.broadcastapp.databinding.FragmentChatroomBinding
import com.kys.broadcastapp.ui.UploadViewModel
import com.kys.broadcastapp.utils.ActivityResultHandlers
import com.kys.broadcastapp.utils.CurrentUserIDProvider
import com.kys.broadcastapp.utils.FireStoreDocumentField
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class ChatroomFragment : Fragment() {

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var isBroadcastChannel: Boolean = false

    private lateinit var binding: FragmentChatroomBinding
    private lateinit var chatFileOptionsBinding: ChatFileOptionsBinding

    private lateinit var activityResultHandlers: ActivityResultHandlers

    @Inject
    lateinit var messageAdapter: MessageAdapter

    private var messageList = arrayListOf<Message>()

    private lateinit var currentUserID: String

    private lateinit var viewModel: ChatroomViewModel
    private lateinit var uploadViewModel: UploadViewModel

    private lateinit var chatroomId: String
    private lateinit var chatroomData: ChatRoom
    private lateinit var chatroomUsersExceptCurrentUser: List<String>

    private var messageType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ChatroomViewModel::class.java]
        uploadViewModel = ViewModelProvider(this)[UploadViewModel::class.java]
        currentUserID = CurrentUserIDProvider.getCurrentUserId()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatroomBinding.inflate(layoutInflater, container, false)
        chatFileOptionsBinding = ChatFileOptionsBinding.inflate(layoutInflater)
        activityResultHandlers = ActivityResultHandlers(this, uploadViewModel)

        try {
            val args = ChatroomFragmentArgs.fromBundle(requireArguments())
            chatroomId = args.chatroomId
        }catch (ex: Exception){
            Log.d("Test", "Error occurred while transferring the chatroomID from BroadcastFragment to ChatroomFragment : \n${ex.message}")
            /* Handel the exception occurred in data passing between fragments */
        }
        viewModel.getChatroomData(chatroomId) {
            it?.let { it1 ->
                chatroomData = it1
                chatroomUsersExceptCurrentUser = chatroomData.users.filter { userID ->
                    userID != CurrentUserIDProvider.getCurrentUserId()
                }
            }
        }

        binding.apply {
            if (chatroomUsersExceptCurrentUser.size > 1) {
                isBroadcastChannel = true
                tvChatroomName.text = chatroomData.name
                Glide
                    .with(ivProfile.context)
                    .load(chatroomData.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(ivProfile)

            } else {
                viewModel.getUserData(chatroomUsersExceptCurrentUser[0]) {
                    it?.let { it1 ->
                        tvChatroomName.text = it1.userName
                        Glide
                            .with(ivProfile.context)
                            .load(it1.image)
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile)
                            .into(ivProfile)
                    }
                }
            }
        }

        bottomSheetDialog = BottomSheetDialog(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            rvMessages.adapter = messageAdapter

            /* back button pressed */
            backArrow.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }

            /* if message is clicked */
            messageAdapter.onClick = { /* Do something like copy, forward, delete, etc */ }

            //viewModel.getChatroomIdList(fetchCurrentUserID()) /* from Api */

            /* fetch and observe the message list */
            viewModel.messageList.observe(viewLifecycleOwner, Observer { messages ->
                messageAdapter.setAdapterList(messages)
                messageList.clear()
                messageList.addAll(messages)
            })
            viewModel.getMessageList(chatroomId)
//            viewModel.getMessageList(fetchCurrentUserID()) /* from Api */

            btnSend.setOnClickListener {
                val message = etMessage.text.toString().trim()
                if (!TextUtils.isEmpty(message)) {
                    messageType = FireStoreDocumentField.MESSAGE_TYPE_TEXT
                    messageType?.let {
                        sendMessageToUsers(message, it, currentUserID, getFormattedTimeStamp())
                    }
                    etMessage.text.clear()
                    hideSoftKeyboard()
                }
            }

            btnAttachment.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(
                        btnAttachment.context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (bottomSheetDialog != null) bottomSheetDialog.show()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
                hideSoftKeyboard()
            }

            uploadViewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
                when (status) {
                    is UploadResult.InProgress -> {
                        // Handle progress update
                        Log.d("Test", "File Upload inProgress ${status.progress}")
                        llProgress.visibility = View.VISIBLE
                        llProgress.progress = status.progress
                    }

                    is UploadResult.Success -> {
                        Log.d("Test", "File Upload Successful")
                        llProgress.visibility = View.GONE
                        val downloadUrl = status.url
                        // Handle upload success and downloadUrl
                        messageType?.let {
                            sendMessageToUsers(
                                downloadUrl,
                                it,
                                currentUserID,
                                getFormattedTimeStamp()
                            )
                        }
                    }

                    is UploadResult.Failure -> {
                        Log.d("Test", "File Upload Failure ${status.error}")
                        llProgress.visibility = View.GONE
                        Toast.makeText(requireContext(), "Failed to Upload the File", Toast.LENGTH_LONG).show()
                    }

                    is UploadResult.Cancelled -> {
                        Log.d("Test", "File Upload Cancelled")
                        llProgress.visibility = View.GONE
                    }
                    // Handle other status cases
                    else -> {
                        Log.d("Test", "Hide Progress")
                        llProgress.visibility = View.GONE
                    }
                }
            }

            uploadViewModel.uploadError.observe(viewLifecycleOwner) { error ->
                Log.d("Test", "File Upload Error \n$error")
                llProgress.visibility = View.GONE
                // Handle upload error
            }

        }

        chatFileOptionsBinding.apply {
            llCamera.setOnClickListener {
                // Camera click listener logic
                bottomSheetDialog.dismiss()
                messageType = FireStoreDocumentField.MESSAGE_TYPE_IMAGE
                val intentCamera = Intent(ACTION_IMAGE_CAPTURE)
                activityResultHandlers.captureImageLauncher.launch(intentCamera)
            }

            llGallery.setOnClickListener {
                // Gallery click listener logic
                bottomSheetDialog.dismiss()
                messageType = FireStoreDocumentField.MESSAGE_TYPE_IMAGE
                val intentImage = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                activityResultHandlers.pickImageLauncher.launch(intentImage)
            }

            llVideo.setOnClickListener {
                // Video click listener logic
                bottomSheetDialog.dismiss()
                messageType = FireStoreDocumentField.MESSAGE_TYPE_VIDEO
                val intentVideo = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                )
                activityResultHandlers.pickVideoLauncher.launch(intentVideo)
            }

            ivClose.setOnClickListener {
                // Close click listener logic
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(view)


    }

    private fun getFormattedTimeStamp(): String {
        // Generate a server timestamp

        val serverTimestamp = FieldValue.serverTimestamp()
        // Format the server timestamp to "yyyy-MM-dd HH:mm:ss"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return dateFormat.format(serverTimestamp)
    }

    private fun sendMessageToUsers(
        message: String,
        messageType: String,
        currentUserID: String,
        formattedTimestamp: String
    ) {
        viewModel.sendMessageToUsers(
            currentUserID,
            chatroomUsersExceptCurrentUser,
            Message(
                message,
                UUID.randomUUID().toString(),
                formattedTimestamp,
                messageType,
                currentUserID,
                false
            )
        )
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = ContextCompat.getSystemService(
            requireContext(),
            InputMethodManager::class.java
        )
        inputMethodManager?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

//    private fun navigateToCreateChatroomFragment() {
//        findNavController().navigate(ChatroomFragmentDirections.actionBroadcastFragmentToCreateChatroomFragment())
//    }
//
//    private fun navigateToChatroomFragment(chatroomId: String) {
//        findNavController().navigate(ChatroomFragmentDirections.actionBroadcastFragmentToChatroomFragment())
//    }

}