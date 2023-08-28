package com.kys.broadcastapp.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.kys.broadcastapp.data.modals.dataModals.User
import com.kys.broadcastapp.repository.FirebaseRepository
import com.kys.broadcastapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var firebaseRepository: FirebaseRepository

    @Inject
    lateinit var userRepository: UserRepository

    fun signOut() {
        userRepository.signOut()
    }

    fun getUser(onComplete: (User?) -> Unit) {
        firebaseRepository.getUser(documentId = firebaseRepository.getCurrentUserID()) {
            onComplete(it)
        }
    }

    fun updateUser(user: User, selectedProfileUri: Uri?, onComplete: (Boolean) -> Unit) {
        if (selectedProfileUri == null) {
            firebaseRepository.apply {
                saveUser(getCurrentUserID(), user) { isUpdate ->
                    onComplete(isUpdate)
                }
            }
        }
        selectedProfileUri?.let { uri ->
            firebaseRepository.apply {
                saveProfileImage(uri) {
                    user.image = it
                    saveUser(getCurrentUserID(), user) { isUpdate ->
                        onComplete(isUpdate)
                    }
                }
            }
        }
    }
}