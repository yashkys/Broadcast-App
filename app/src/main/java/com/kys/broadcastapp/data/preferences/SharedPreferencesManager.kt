package com.kys.broadcastapp.data.preferences

import androidx.appcompat.app.AppCompatActivity
import com.kys.broadcastapp.MainActivity

object SharedPreferencesManager {

    private val manager by lazy {
        MainActivity.activity.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)
    }
    private val shEditor by lazy {
        manager.edit()
    }

    fun setKeyValue(key: String, value: String) {
        shEditor.putString(key, value).apply()
    }

    fun getValue(key: String): String? {
        return manager.getString(key, null)
    }

    fun deleteKey(key:String){
        shEditor.remove(key).apply()
    }
}