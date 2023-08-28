package com.kys.broadcastapp.ui.broadcast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kys.broadcastapp.data.modals.dataModals.ChatRoom
import com.kys.broadcastapp.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BroadcastViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var firebaseRepository: FirebaseRepository

    private var _chatroomIdList = MutableLiveData<List<String>>()
    var chatroomIdList: LiveData<List<String>> = _chatroomIdList

    private var _chatroomList = MutableLiveData<List<ChatRoom>>()
    var chatroomList: MutableLiveData<List<ChatRoom>> = _chatroomList

    fun getChatroomList(currentUserId: String) {
        firebaseRepository.apply {
            fetchBroadcastChannelIDList(currentUserId) {
                it?.let { chatroomIDList1 ->
                    _chatroomIdList.value = chatroomIDList1
                    val crl = arrayListOf<ChatRoom>()
                    for (chatroomID in chatroomIDList1) {
                        fetchChatroomData(chatroomID) { chatroomData ->
                            chatroomData?.let { chatroomData1 ->
                                crl.add(chatroomData1)
                            }
                        }
                    }
                    chatroomList.value = crl
                }
            }
        }
        Log.d("Test", " Chat Users List : ${_chatroomIdList.value}")
    }


}