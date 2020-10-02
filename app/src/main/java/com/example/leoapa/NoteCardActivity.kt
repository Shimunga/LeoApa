package com.example.leoapa

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class NoteCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_card)
    }

    fun onClickSaveNoteBtn(view: View) {
        val result = Intent().apply {
            putExtra(MainActivity.ENTRY_RESULT, "Saved")
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    fun onClickCancelBtn(view: View) {
        val result = Intent().apply {
            putExtra(MainActivity.ENTRY_RESULT, "Cancelled")
        }
        setResult(Activity.RESULT_CANCELED, result)
        finish()
    }
}