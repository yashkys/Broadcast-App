package com.kys.broadcastapp.ui.createBroadCastGroup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.data.modals.dataModals.ResponseBroadcastChannelListModal
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Objects
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateNewBroadcastGroupViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var firebaseRepository: FirebaseRepository

    private var _userIDList = MutableLiveData<List<String>>()
    var userIDList: LiveData<List<String>> = _userIDList

    private var _userList = MutableLiveData<List<User>>()
    var userList: MutableLiveData<List<User>> = _userList

    fun getUserList(currentUserID: String) {
        firebaseRepository.apply {
            fetchFriendUserIDList(currentUserID) {
                Log.d("Test", " Friends List before assigning : ${it?.friends}")
                val friends = it?.friends //friend users id list
                Log.d("Test", " Friends List : $friends")
                friends?.let { friendIds ->
                    _userIDList.value = friendIds
                    val ul = arrayListOf<User>()
                    for (userID in friendIds) {
                        fetchUserData(userID) { userData ->
                            userData?.let { userData1 ->
                                Log.d("Test", " userData of id: $userID : $userData1")
                                ul.add(userData1)
                                Log.d("Test", " ul List : $ul")
                                userList.value = ul
                                Log.d("Test", " Friends List : ${_userList.value}")
                            }
                        }
                    }

                }

            }
        }
    }

    fun getCurrentUserData(currentUserID: String, onComplete: (User?) -> Unit) {
        firebaseRepository.getUser(currentUserID, onComplete)
    }

    fun createBroadcastGroup(
        currentUserID: String,
        groupName: String,
        image: String,
        selectedUserList: ArrayList<User>,
        onComplete: (Boolean) -> Unit
    ) {
        val selectedUserIdList = arrayListOf<String>()
        for (user in selectedUserList) {
            selectedUserIdList.add(user.userID)
        }

        val newBroadcastChatroom = ChatRoom(
            UUID.randomUUID().toString(),
            groupName,
            image,
            "",
            "This is a broadcast channel.",
            selectedUserIdList,
            arrayListOf(),
            null
        )
        firebaseRepository.apply {
            createChatroom(newBroadcastChatroom) { isCreated ->
                Log.d("Test", "Chatroom Created :$isCreated")
                //fetch broadcastChannel list for this user
                fetchBroadcastChannelIDList(currentUserID) { response ->
                    Log.d("Test", "Response fetched : $response")
                    // save in broadcastChannel
                    var chatroom = arrayListOf<String>()
                    response?.chatroomIds?.let {  // if response list is not empty then arr them in this list to concatenate data
                        chatroom = it
                    }
                    chatroom.add(newBroadcastChatroom.id)
                    try {
                        saveChatroomIdBroadcastChannel(
                            currentUserID,
                            ResponseBroadcastChannelListModal(chatroom)
                        ) {
                            Log.d(
                                "Test",
                                "saved chatroom created in the broadcast channel collection"
                            )
                        }
                    } catch (e: Exception) {
                        Log.d(
                            "Test",
                            "Unable to save : ${e.message}"
                        )
                    } finally {
                        onComplete(isCreated)
                    }

                }
            }

        }
    }

}