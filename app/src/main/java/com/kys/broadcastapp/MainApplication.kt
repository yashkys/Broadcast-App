package com.kys.broadcastapp

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.kys.broadcastapp.data.modals.dataModals.User
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {

    var user: User? = null
    companion object {
        var instance: MainApplication? = null
        val context: Context?
            get() = instance
    }

    override fun onCreate() {
        context?.let { FirebaseApp.initializeApp(it) }
        instance = this
        super.onCreate()
    }
}