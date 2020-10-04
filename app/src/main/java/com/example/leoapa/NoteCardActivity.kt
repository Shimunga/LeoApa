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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_card)
        dataItemMode = intent.getSerializableExtra("DataItemMode") as DataItemMode

        noteTitleEd.setText(RandomData.randomTitle)
        noteEd.setText(RandomData.randomLorem)
    }

    fun onClickSaveNoteBtn(view: View) {
        val itemNew = NotesItem(
            noteTitleEd.text.toString(),
            noteEd.text.toString()
        )
        //notesItemList.add(0, itemNew) //RandomData.randomItem

        //repaints all elements
        //mainItemsGrd.adapter?.notifyDataSetChanged()

        //repaints only inserted at the position specified
//        mainItemsGrd.adapter?.notifyItemInserted(0) //0 - cause inserted at frst postition (see above)
//        mainItemsGrd.smoothScrollToPosition(0) //as only first was repainted, view is still on previous position. This will scroll to first - newly inserted
//        itemEd.setText(RandomData.randomTitle)

        //save to db
        itemNew.uid = db.notesItemDao().insertAll(itemNew).first()

        val result = Intent().apply {
            putExtra("NotesItem", itemNew)
            putExtra("DataItemMode", dataItemMode)
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