package com.example.leoapa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.leobase.BaseActivity
import com.example.leobase.ConvertUtils
import kotlinx.android.synthetic.main.activity_note_card.*

class NoteCardActivity : BaseActivity() {

    private val db get() = Database.getInstance(this)
    private var dataItemMode: DataItemMode = DataItemMode.dimNone
    private var item: NotesItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_card)

        when {
            intent?.action == Intent.ACTION_SEND -> {
                processShareRequest()
            }
            else -> {
                if (intent.getSerializableExtra("DataItemMode") != null) { //in case if not called from MainActivity, eg. share request, then no such serializableExtra exists
                    dataItemMode = (intent.getSerializableExtra("DataItemMode") as DataItemMode)
                }
                fillDataFields()
            }
        }
    }
    private fun processShareRequest(){
        if ("text/plain" == intent.type) {
            dataItemMode = DataItemMode.dimInsert
            intent.getStringExtra(Intent.EXTRA_SUBJECT)?.let {noteTitleEd.setText(it)}
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {noteEd.setText(it)}
        }
    }

    private fun fillDataFields(){
        when (dataItemMode){
            DataItemMode.dimInsert -> {
                noteTitleEd.setText(RandomData.randomTitle)
                noteEd.setText(RandomData.randomLorem)
            }
            DataItemMode.dimEdit -> {
                item = intent.getSerializableExtra("DataItem") as NotesItem
                noteTitleEd.setText(item?.title)
                noteEd.setText(item?.text)
                if (item?.img != null) {
                    val options = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeByteArray(item?.img, 0, item?.img?.size!!, options)
                    imageEd.setImageBitmap(bitmap)
                }
            }
        }
        setupUI()
    }

    fun onClickSaveNoteBtn(view: View) {
        item = item ?: NotesItem("", "" ) //item initially will be null because of insert mode (in contrary edit mode when old data is available)
        item?.title = noteTitleEd.text.toString()
        item?.text = noteEd.text.toString()

        item?.img = ConvertUtils.drawableToByteArray(imageEd.drawable)

        //save to db
        when (dataItemMode) {
            DataItemMode.dimInsert ->  item!!.uid = db.notesItemDao().insertAll(item!!).first()
            DataItemMode.dimEdit ->  db.notesItemDao().update(item!!)
        }

        val result = Intent().apply {
            putExtra("DataItemMode", dataItemMode)
            putExtra("NotesItem", item)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    fun onClickCancelBtn(view: View) {
        val result = Intent()
        setResult(Activity.RESULT_CANCELED, result)
        finish()
    }

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

companion object {
    //image pick code
    private const val IMAGE_PICK_CODE = 1000

        //Permission code
    private const val PERMISSION_CODE = 1001
    }
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

    //handle requested permission result
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
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageEd.setImageURI(data?.data)
            setupUI()
        }
    }

    fun onClickDeleteImageBtn(view: View) {
        if ((dataItemMode == DataItemMode.dimInsert || dataItemMode == DataItemMode.dimEdit) && (imageEd.drawable != null)){
            imageEd.setImageDrawable(null)
            setupUI()
        }
    }

    private fun setupUI(){
        if ((dataItemMode == DataItemMode.dimInsert || dataItemMode == DataItemMode.dimEdit) && imageEd.drawable != null) {
            deleteImageBtn.visibility = View.VISIBLE
        }else{
            deleteImageBtn.visibility = View.GONE
        }
    }
}