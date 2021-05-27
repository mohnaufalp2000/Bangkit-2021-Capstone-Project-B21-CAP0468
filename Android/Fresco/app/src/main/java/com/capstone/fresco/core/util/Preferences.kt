package com.capstone.fresco.core.util

import android.content.Context

class Preferences(val context: Context) {

    companion object{
        const val KEY = "key"
    }

    var sharedPreferences = context.getSharedPreferences(KEY, 0)

    fun setState(key: String, value: String){
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    fun getState(key: String) : String? =
        sharedPreferences.getString(key, "")

}