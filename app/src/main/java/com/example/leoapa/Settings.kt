package com.example.leoapa

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

public enum class AppParams(var userString: String) {
    prmLayoutMode("LAYOUT_STYLE_PARAM"),
    prmLang("LANGUAGE_PARAM")
}

public class Settings(activity: Activity) {
    companion object {
        const val PREFERENCES_FILE = "leoapa_preferences"
    }

    private val contextActivity = activity
    private val preferences: SharedPreferences = contextActivity.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    init {
         // clearParam()
    }

    public fun retrieveParamString(param: AppParams): String? {
        return preferences.getString(param.userString, "en")
    }
    public fun retrieveParamBool(param: AppParams): Boolean {
        return preferences.getBoolean(param.userString, false)
    }

    public fun saveParam(param: AppParams, value: Any) {
        val editor: SharedPreferences.Editor = preferences.edit()
        when (value) {
            is Boolean -> editor.putBoolean(param.userString, value as Boolean)
            is String -> editor.putString(param.userString, value as String)
            is Int -> editor.putInt(param.userString, value as Int)
        }
        editor.apply()
    }

    public fun clearParam(){
        preferences.edit().clear().commit()
    }
}