package com.example.leoapa

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_note_card.*

class NoteCardActivity : AppCompatActivity() {

    private val db get() = Database.getInstance(this)
    private var dataItemMode: DataItemMode = DataItemMode.dimNone
    private var item: NotesItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_card)
        dataItemMode = intent.getSerializableExtra("DataItemMode") as DataItemMode
        when (dataItemMode){
            DataItemMode.dimInsert -> {
                noteTitleEd.setText(RandomData.randomTitle)
                noteEd.setText(RandomData.randomLorem)
            }
            DataItemMode.dimEdit -> {
                item = intent.getSerializableExtra("DataItem") as NotesItem
                noteTitleEd.setText(item?.title)
                noteEd.setText(item?.text)
            }
        }
    }

    fun onClickSaveNoteBtn(view: View) {
        item = item ?: NotesItem("", "", 0) //item initially will be null because of insert mode (in contrary edit mode when old data is available)
        item?.title = noteTitleEd.text.toString()
        item?.text = noteEd.text.toString()

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
}