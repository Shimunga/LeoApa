package com.example.leoapa

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * The class contains save/retrieve functions for app's params
 */
class Settings(context: Context) {
//region Variables, constants definition
    companion object {
        const val PREFERENCES_FILE = "leoapa_preferences"
    }
    private val contextActivity = context
    //preferences persistence object
    private val preferences: SharedPreferences =
        contextActivity.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
//endregion


    init {
        Log.i("test", "settings init")
        //when params must be cleared, take the comments off
        // clearParam()
    }

    /**
     * The function gets the string param from the prefs
     */
    fun retrieveParamString(param: AppParams): String? {
        return preferences.getString(param.userString, "en")
    }

    /**
     * The function gets the boolean param from the prefs
     */
    fun retrieveParamBool(param: AppParams): Boolean {
        return preferences.getBoolean(param.userString, false)
    }

    /**
     * The function saves param to the prefs
     * @param param param to be saved
     * @param value param's value
     */
    fun saveParam(param: AppParams, value: Any) {
        val editor: SharedPreferences.Editor = preferences.edit()
        when (value) {
            is Boolean -> editor.putBoolean(param.userString, value as Boolean)
            is String -> editor.putString(param.userString, value as String)
            is Int -> editor.putInt(param.userString, value as Int)
            else -> throw IllegalArgumentException("Not imlemented yet...")
        }
        editor.apply()
    }

    /**
     * Utility function clears all param file up
     */
    fun clearParam(){
        preferences.edit().clear().commit()
    }
}