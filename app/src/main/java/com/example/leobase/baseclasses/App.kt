package com.example.leobase

import android.app.Application
import com.example.leoapa.AppParams
import com.example.leoapa.Settings
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        var settings = Settings(this)

        BaseActivity.settings = settings

        val lng = settings.retrieveParamString(AppParams.prmLang)

        BaseActivity.locale = Locale(lng) //set any locale you want here
        BaseActivity.nightTheme = settings.retrieveParamBool(AppParams.prmTheme)
    }

}