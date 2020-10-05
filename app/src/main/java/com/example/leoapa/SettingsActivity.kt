package com.example.leoapa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class SettingsActivity : BaseActivity() {
    //private var settings: Settings? = null
    lateinit var spinner: Spinner
    private var isSpinerSetup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
      //  settings = Settings(this)

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
        staggLinearSwitch.isChecked = settings?.retrieveParamBool(AppParams.prmLayoutMode)!!

        isSpinerSetup = true
        spinner.setSelection(UIutils.getSpinnerIndexByString(spinner, settings?.retrieveParamString(AppParams.prmLang)!!));
    }

    private fun setupLanguage() {
        //currentLanguage = intent.getStringExtra(currentLang).toString()
        spinner = findViewById(R.id.langSel)
        val list = ArrayList<String>()
        list.add(getString(R.string.SelectLanguage))
        list.add("en")
        list.add("lv")
        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(1);
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                    }
                    1 -> {
                        settings?.saveParam(AppParams.prmLang, "en")
                        restartApp()
                    }
                    2 -> {
                        settings?.saveParam(AppParams.prmLang, "lv")
                        restartApp()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun restartApp(){
        if (isSpinerSetup) {
            isSpinerSetup = false
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle(this.getString(R.string.msgInformationTile))
            .setMessage(this.getString(R.string.msgLangChangeConfirmation))
            .setPositiveButton(this.getString(R.string.Ok)) { _, _ -> }
        val dialog = builder.create()
        dialog.show()

//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        finish()
//        this.startActivity(intent)
//        Runtime.getRuntime().exit(0)

//        val intent = intent
//        finish()
//        startActivity(intent)
    }

 }