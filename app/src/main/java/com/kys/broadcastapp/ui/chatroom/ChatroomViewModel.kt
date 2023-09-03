package com.kys.broadcastapp.ui.chatroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.data.modals.dataModals.Message
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.data.modals.responseModals.ResponseChatroomMessageListModal
import com.kys.broadcastapp.repository.FirebaseRepository
import com.kys.broadcastapp.utils.FireStoreDocumentField
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

    fun getMessageList(chatroomId: String) {
        firebaseRepository.apply {
            fetchMessageIDList(chatroomId) {
                it?.let { response ->
                    _messageIDList.value = response.messageIds!!
                    val ml = arrayListOf<Message>()
                    for (messageID in response.messageIds!!) {
                        fetchMessageData(messageID) { messageData ->
                            messageData?.let { messageData1 ->
                                ml.add(messageData1)
                                messageList.value = ml
                            }
                        }
                    }
                }
            }
        }
        Log.d("Test", " Message List : ${_messageList.value}")
    }

    fun getUserData(userId: String, onComplete: (User?) -> Unit) {
        firebaseRepository.getUser(userId, onComplete)
    }


    fun updateChatroomData(
        chatroomData: ChatRoom,
        message: Message,
        onComplete: (Boolean) -> Unit
    ) {
        chatroomData.lastMessage = message
        firebaseRepository.apply {
            saveChatroom(chatroomData) {
                onComplete(it)
            }
            fetchMessageIDList(chatroomData.id) {
                it?.let { response ->
                    response.messageIds?.add(message.messageID)
                    saveMessageIDList(chatroomData.id, response) {
                        Log.d("Test", "Saved Message id in chatroom_message collection")
                        onComplete(it)
                    }
                }
//                saveMessageIDList(
//                    chatroomData.id,
//                    ResponseChatroomMessageListModal(arrayListOf(message.messageID))
//                ) {
//                    Log.d("Test", "Saved Message id in chatroom_message collection")
//                    onComplete(true)
//                }

            }
        }
    }

    fun sendMessageToUsers(
        currentUserID: String,
        chatroomData: ChatRoom,
        message: Message,
        userIds: ArrayList<String>
    ) {
        firebaseRepository.apply {
            saveMessage(message)
            for (userId in userIds) {
                getChatroomRoomID(currentUserID, userId) { chatroomId ->
                    fetchChatroomData(chatroomId){
                        it?.let { personalChatroom ->
                            updateChatroomData(personalChatroom, message){
                                Log.d("Test", "Saved Message id in chatroom_message collection for personal chatroom")
                                /*fetchMessageIDList(personalChatroom.id) {
                                    it?.let { response ->
                                        response.messageIds?.add(message.messageID)
                                        saveMessageIDList(personalChatroom.id, response) {
                                            Log.d("Test", "Saved Message id in chatroom_message collection for personal chatroom")
                                        }
                                    }
                                    saveMessageIDList(
                                        personalChatroom.id,
                                        ResponseChatroomMessageListModal(arrayListOf(message.messageID))
                                    ) {
                                        Log.d("Test", "Saved Message id in chatroom_message collection for personal chatroom")
                                    }
                                }*/
                            }
                        }

                    }
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