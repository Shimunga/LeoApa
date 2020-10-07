package com.example.leoapa

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.leobase.BaseActivity
import com.example.leobase.UIutils
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

/**
 * The class contains UI controls for changing app's settings
 */
class SettingsActivity : BaseActivity() {
//region Variables, constants definition
    lateinit var spinner: Spinner //select view for choosing language
    /**
     * Flag true - when setting value for lang. list - to avoid firing selection event handler up
     */
    private var isSpinerSetup: Boolean = false
//endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupLanguage()

        //define event handler for items list visualisation style change
        staggLinearSwitch.setOnCheckedChangeListener { _, isLinearStaggered ->
            setLayoutModeSetting(isLinearStaggered)}

        //define event handler for day/night style change
        themeSwitch.setOnCheckedChangeListener { _, isNight ->
            setThemeSetting(isNight)}

        applySettingsOnStart()
    }

    /**
     * The function saves list visualisation mode to prefs.
     * @param isLinearStaggered true - linear (classic list) style, false - staggered cards
     */
    private fun setLayoutModeSetting(isLinearStaggered: Boolean){
        if(!staggLinearSwitch.isPressed()) { return } //no need to fire event when setting up controls when view opening

        settings?.saveParam(AppParams.prmLayoutMode, isLinearStaggered)
        //nothing to do because this setting will be used in other activity
    }

    /**
     * The function saves to prfs if night mode is chosen.
     * @param isNight true - night mode, otherwise - day
     */
    private fun setThemeSetting(isNight: Boolean){
        if(!themeSwitch.isPressed()) { return } //no need to fire event when setting up controls when view opening

        settings?.saveParam(AppParams.prmTheme, isNight)

        UIutils.showInfo(this, getString(R.string.msgThemeChangeConfirmation))
    }

    /**
     * The functions applies the earlier saved settings to UI controls in order to depict current state
     */
    private fun applySettingsOnStart(){
        staggLinearSwitch.isChecked = settings?.retrieveParamBool(AppParams.prmLayoutMode)!!

        isSpinerSetup = true
        spinner.setSelection(UIutils.getSpinnerIndexByString(spinner, settings?.retrieveParamString(AppParams.prmLang)!!));

        themeSwitch.isChecked = settings?.retrieveParamBool(AppParams.prmTheme)!!
    }

    /**
     * The function sets the language control up and defines selection delegate fuctions
     */
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

    /**
     * The function restarts the app
     */
    fun restartApp(){
        if (isSpinerSetup) {
            isSpinerSetup = false
            return
        }
        UIutils.showInfo(this, this.getString(R.string.msgLangChangeConfirmation))
        //TODO restart doesnt work - must be recoded
        //        val intent = Intent(this, MainActivity::class.java)
        //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //        finish()
        //        this.startActivity(intent)
        //        Runtime.getRuntime().exit(0)
        //        val intent = intent
        //        finish()
        //        startActivity(intent)
    }

    /**
     * The function defines what happens when back button is pressed
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
 }