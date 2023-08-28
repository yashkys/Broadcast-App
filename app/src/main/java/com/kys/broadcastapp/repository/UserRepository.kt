package com.kys.broadcastapp.repository

import com.kys.broadcastapp.data.preferences.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.kys.broadcastapp.MainApplication
import com.kys.broadcastapp.data.modals.dataModals.User
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@ViewModelScoped
class UserRepository @Inject constructor(
    private val repository: FirebaseRepository,
    private val auth: FirebaseAuth
) {
    fun saveUserId() {
        auth.currentUser?.uid?.let {
            SharedPreferencesManager.setKeyValue("userId", it)
        }
    }

    fun getUserId(): String? {
        return SharedPreferencesManager.getValue("userId")
    }

    fun getUser(onComplete: (User?) -> Unit) {
        getUserId()?.let { userId ->
            repository.getUser(userId) {
                MainApplication.instance?.user = it
                onComplete(it)
            }
        } ?: onComplete(null)
    }

    fun signOut() {
        SharedPreferencesManager.deleteKey("userId")
        auth.signOut()
    }
}