package com.example.leoapa

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class SettingsActivity : AppCompatActivity() {
    lateinit var spinner: Spinner
    private var settings: Settings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        settings = Settings(this)

        setupLanguage()

        staggLinearSwitch.setOnCheckedChangeListener { _, isLinearStaggered ->
            setLayoutModeSetting(isLinearStaggered)}

        applySettings()
    }

    private fun setLayoutModeSetting(isLinearStaggered: Boolean){
        settings?.saveParam(AppParams.prmLayoutMode, isLinearStaggered)
        staggLinearSwitch.isChecked = settings?.retrieveParamBool(AppParams.prmLayoutMode)!!
    }

    private fun applySettings(){
        val bool = settings?.retrieveParamBool(AppParams.prmLayoutMode)
        staggLinearSwitch.isChecked = bool!!
        //setLocale(settings!!.retrieveParamString(AppParams.prmLang)!!)
        //TODO Set locale combo box value from preferences comes here
    }

    private fun setupLanguage() {
        //currentLanguage = intent.getStringExtra(currentLang).toString()
        spinner = findViewById(R.id.langSel)
        val list = ArrayList<String>()
        list.add("Select Language")
        list.add("English")
        list.add("Latvian")
        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                    }
                    1 -> {
                        settings?.saveParam(AppParams.prmLang, "en")
                        //setLocale("en")
                    }
                    2 -> {
                        settings?.saveParam(AppParams.prmLang, "lv")
                        //setLocale("lv")
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
/*
    lateinit var locale: Locale
    private var currentLanguage = "en"
    private var currentLang: String? = null
    private fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = locale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(
                this,
                MainActivity::class.java
            )
            refresh.putExtra(currentLang, localeName)
            startActivity(refresh)
        } else {
            Toast.makeText(this@SettingsActivity, "This language is already selected!", Toast.LENGTH_SHORT).show();
        }
    }

 */
}