package com.kys.broadcastapp.ui.chatroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.data.modals.dataModals.Message
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatroomViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var firebaseRepository: FirebaseRepository

    private var _messageIDList = MutableLiveData<List<String>>()
    var messageIDList: LiveData<List<String>> = _messageIDList

    private var _messageList = MutableLiveData<List<Message>>()
    var messageList: MutableLiveData<List<Message>> = _messageList

    private var _chatroomData = MutableLiveData<ChatRoom>()
    var chatroomData: LiveData<ChatRoom> = _chatroomData

    fun getMessageList(chatroomId: String) {
        firebaseRepository.apply {
            fetchMessageIDList(chatroomId) {
                it?.let { messageIDList1 ->
                    _messageIDList.value = messageIDList1
                    val ml = arrayListOf<Message>()
                    for (messageID in messageIDList1) {
                        fetchMessageData(messageID) { messageData ->
                            messageData?.let { messageData1 ->
                                ml.add(messageData1)
                            }
                        }
                    }
                    messageList.value = ml
                }
            }
        }
        Log.d("Test", " Message List : ${_messageList.value}")
    }

    fun getChatroomData(chatroomId: String) {
        firebaseRepository.fetchChatroomData(chatroomId){
            it?.let { _chatroomData.value = it }
        }
    }

    fun getUserData(userId: String, onComplete: (User?) -> Unit) {
        firebaseRepository.getUser(userId, onComplete)
    }

    fun sendMessageToUsers(currentUserID: String, userIds: List<String>, message: Message) {
        firebaseRepository.apply {
            sendMessageInMessageCollection(message)
            for (userId in userIds) {
                getChatroomRoomID(currentUserID, userId) { chatroomId ->
                    sendMessageInChatroom(chatroomId, message.messageID)
                    saveMessage(message)
                }
            }
        }
    }

/*
    private lateinit var activityResultHandlers: ActivityResultHandlers

    fun pickVideo(){
        val intentVideo = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
        activityResultHandlers.pickVideoLauncher.launch(intentVideo)

    }

    fun pickImage() {
        val intentImage = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultHandlers.pickImageLauncher.launch(intentImage)
    }

    fun captureImage() {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultHandlers.captureImageLauncher.launch(intentCamera)

    }
    */

}