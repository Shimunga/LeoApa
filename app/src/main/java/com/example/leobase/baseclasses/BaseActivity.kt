package com.example.leobase

import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import com.example.leoapa.R
import com.example.leoapa.Settings
import java.util.*

/**
 * The class is base for all apps activities in order to set common locale and theme.
 */
open class BaseActivity : AppCompatActivity() {
    companion object {
        var settings: Settings? = null //settings instance
        var locale: Locale? = null //current locale retrieved from settings
        var nightTheme: Boolean = false //current theme  retrieved from settings
    }

    init {
        updateLocale(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (nightTheme) {
            setTheme(R.style.ThemeOverlay_AppCompat_Dark_Leoapa)
        }else{
            setTheme(R.style.ThemeOverlay_AppCompat_DayNight)
        }
        super.onCreate(savedInstanceState)
    }

    /**
     * The function updates app's locale
     */
    private fun updateLocale(wrapper: ContextThemeWrapper) {
        if(locale ==Locale("") ) // Do nothing if dLocale is null
            return

        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.setLocale(locale)
        wrapper.applyOverrideConfiguration(configuration)
    }
}