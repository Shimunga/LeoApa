package com.example.leoapa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.leobase.BaseActivity
import com.example.leobase.ConvertUtils
import kotlinx.android.synthetic.main.activity_note_card.*

/**
 * Edit/Input notes activity class.
 */
class NoteCardActivity : BaseActivity() {

//region Variables, constants definition
    private val db get() = Database.getInstance(this)
    //The state of note being operated with - edit or insert
    private var dataItemMode: DataItemMode = DataItemMode.dimNone
    //The note to be edited or inserted
    private var item: NotesItem? = null
    companion object {
        //code for gallery activity call
        private const val IMAGE_PICK_CODE = 1000
        //Permission code for gallery
        private const val PERMISSION_CODE = 1001
    }
//endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_card)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        when {
                //what happens if smb shares info with the app
                intent?.action == Intent.ACTION_SEND -> {
                    processShareRequest()
            }
            else -> {
                //if happens if activity is called from the list (edit, insert)
                if (intent.getSerializableExtra("DataItemMode") != null) { //in case if not called from MainActivity, eg. share request, then no such serializableExtra exists
                    dataItemMode = (intent.getSerializableExtra("DataItemMode") as DataItemMode)
                }
                fillDataFields()
            }
        }
    }

    /**
     * The event handler function fires up when back button is clicked
    */
    override fun onSupportNavigateUp(): Boolean {
        saveData()
        return true
    }

    /**
     * The function processes share request from other app - inserts new note
     */
    private fun processShareRequest(){
        if ("text/plain" == intent.type) {
            dataItemMode = DataItemMode.dimInsert
            intent.getStringExtra(Intent.EXTRA_SUBJECT)?.let {noteTitleEd.setText(it)}
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {noteEd.setText(it)}
        }
    }

    /**
     * The function fills data when the activity is opened. Fill depends on current CRUD operation
     */
    private fun fillDataFields(){
        when (dataItemMode){
            DataItemMode.dimInsert -> {
                //in insert mode, for sake of test simplicity, generate data for some fields
                if (settings?.retrieveParamBool(AppParams.prmRandomData) == true) {
                    noteTitleEd.setText(RandomData.randomTitle)
                    noteEd.setText(RandomData.randomLorem)
                }
            }
            DataItemMode.dimEdit -> {
                //in edit mode looks in intent's extra for an item
                item = intent.getSerializableExtra("DataItem") as NotesItem
                noteTitleEd.setText(item?.title)
                noteEd.setText(item?.text)

                //in order to get the image - converts BytesArray to drawable
                imageEd.setImageDrawable(ConvertUtils.byteArrayToDrawable(item?.img))
            }

            else -> {}
        }
        setupUI()
    }

    /**
     * Save button event handler
     */
    fun onClickSaveNoteBtn(view: View) {
        saveData()
    }

    /**
     * The function saves edited or inserted data to database and closes tha activity
     */
    private fun saveData() {
        item = item ?: NotesItem("", "") //item initially will be null because of insert mode (in contrary edit mode when old data is available)
        item?.title = noteTitleEd.text.toString()
        item?.text = noteEd.text.toString()

        item?.img = ConvertUtils.drawableToByteArray(imageEd.drawable)

        //save to db
        when (dataItemMode) {
            DataItemMode.dimInsert -> item!!.uid = db.notesItemDao().insertAll(item!!).first()
            DataItemMode.dimEdit -> db.notesItemDao().update(item!!)
            else -> {}
        }

        val result = Intent().apply {
            putExtra("DataItemMode", dataItemMode)
            putExtra("NotesItem", item)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    /**
     * Cancel button event handler - closes the activity - no data being saved
     */
    fun onClickCancelBtn(view: View) {
        val result = Intent()
        setResult(Activity.RESULT_CANCELED, result)
        finish()
    }

    /**
     * Share button event handler shares title and descriptin text with ACTION_SEND
     */
    fun onClickShareBtn(view: View) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, noteTitleEd.text.toString())
            putExtra(Intent.EXTRA_TEXT, noteEd.text.toString())
            type = "text/plain"

 //TODO doesnt work yet - throws exception - no idea...
//            val bmpUri = ConvertUtils.getLocalBitmapUri(imageEd)
//            if (bmpUri != null) {
//                // Construct a ShareIntent with link to image
//                putExtra(Intent.EXTRA_STREAM, bmpUri)
//                type = "image/*"
//                }
        }
        startActivity(sendIntent)
    }

//region Image picking functions
    /**
     * LoadImageBtn event handler asks for gallery permissions and picks the image up
     */
    fun onClickLoadImageBtn(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else{
                //permission already granted
                pickImageFromGallery()
            }
        }
        else{
            //system OS is < Marshmallow
            pickImageFromGallery()
        }
    }

    /**
     * Permission request handler - picks image or denies
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, getString(R.string.msgInfoPermissionDenied), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * The function picks images from gallery by strting image picking activity
     */
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    /**
     * Activity result event handler processes result of picked image by filling image view
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageEd.setImageURI(data?.data)
            setupUI()
        }
    }
//endregion

    /**
     * Delete image button event handler clears the image from image view
     */
    fun onClickDeleteImageBtn(view: View) {
        if ((dataItemMode == DataItemMode.dimInsert || dataItemMode == DataItemMode.dimEdit) && (imageEd.drawable != null)){
            imageEd.setImageDrawable(null)
            setupUI()
        }
    }

    /**
     * The function sets up visual controls depending on input form and data fields state
     */
    private fun setupUI(){
        //sets delete image button up depending on form's CRUD and image field state
        if ((dataItemMode == DataItemMode.dimInsert || dataItemMode == DataItemMode.dimEdit) && imageEd.drawable != null) {
            deleteImageBtn.visibility = View.VISIBLE
        }else{
            deleteImageBtn.visibility = View.GONE
        }

        //sets the action bar title up depending on form's CRUD state
        when (dataItemMode) {
            DataItemMode.dimInsert -> {supportActionBar!!.title = "New Note"}
            DataItemMode.dimEdit -> {supportActionBar!!.title = "Edit Note"}
            else -> {}
        }
    }
}