package com.kys.broadcastapp.ui.createBroadCastGroup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
                it?.let { userIDList1 ->
                    _userIDList.value = userIDList1
                    val ul = arrayListOf<User>()
                    for (userID in userIDList1) {
                        fetchUserData(userID) { userData ->
                            userData?.let { userData1 ->
                                ul.add(userData1)
                            }
                        }
                    }
                    userList.value = ul
                }

                Log.d("Test","Fetched friends user Ids: []")
            }
        }
        Log.d("Test", " Friends List : ${_userList.value}")
    }

    fun getCurrentUserData(currentUserID: String, onComplete: (User?) -> Unit) {
        firebaseRepository.getUser(currentUserID, onComplete)
    }

    fun createBroadcastGroup(
        groupName: String,
        image: String,
        selectedUserList: ArrayList<User>,
        onComplete: (Boolean) -> Unit
    ) {
        val selectedUserIdList = arrayListOf<String>()
        for(user in selectedUserList){
            selectedUserIdList.add(user.userID)
        }

        firebaseRepository.createChatroom(
            ChatRoom(
                UUID.randomUUID().toString(),
                groupName,
                image,
                "",
                "This is a broadcast channel.",
                selectedUserIdList,
                arrayListOf(),
                null
            )
        ){
            onComplete(it)
        }
    }
}