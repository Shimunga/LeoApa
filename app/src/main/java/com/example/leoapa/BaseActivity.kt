package com.example.leoapa

import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

open class BaseActivity : AppCompatActivity() {
    companion object {
        public var dLocale: Locale? = null
        public var settings: Settings? = null
        public var dNightTheme: Boolean = false
    }

    init {
        updateConfig(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        if (dNightTheme) {
            setTheme(R.style.ThemeOverlay_AppCompat_Dark_Leoapa)
        }else{
            setTheme(R.style.ThemeOverlay_AppCompat_DayNight)
        }
        super.onCreate(savedInstanceState)
    }
    fun updateConfig(wrapper: ContextThemeWrapper) {
        if(dLocale==Locale("") ) // Do nothing if dLocale is null
            return

        Locale.setDefault(dLocale)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }
}