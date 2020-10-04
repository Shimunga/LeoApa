package com.example.leoapa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), AdapterEventListener {
 //region variables, constants definition
   companion object {
      const val ENTRY_INTENT = 100
      const val PREFERENCES_INTENT = 110
   }
   private val notesItemList = NotesItemList()//mutableListOf<NotesItem>()
   private val db get() = Database.getInstance(this)
   private var settings: Settings? = null
 //endregion

 //region functions, eventhandlers
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      settings = Settings(this)


      //load from database
      notesItemList.addAll(db.notesItemDao().getAll())

      //setup adapter
      val adapter =
         NotesListAdapter(
            this,
            notesItemList
         )
     mainItemsGrd.adapter = adapter
     applySettings();
   }

    private fun applySettings(){
        switchLayouts(settings?.retrieveParamBool(AppParams.prmLayoutMode)!!)
        //setLocale(settings?.retrieveParamString(AppParams.prmLang)!!)
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
            Toast.makeText(this@MainActivity, "This language is already selected!", Toast.LENGTH_SHORT).show();
        }
    }
*/
   private fun switchLayouts(isLinearStaggered: Boolean) {
      Log.v("Switch State=", "" + isLinearStaggered)

      if (isLinearStaggered) {
         mainItemsGrd.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
      }else {
         mainItemsGrd.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
         (mainItemsGrd.layoutManager as StaggeredGridLayoutManager).setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE)
      }
   }

   fun onClickOpenConfigBtn(v: View) {
       val intent = Intent(this, SettingsActivity::class.java)
       startActivityForResult(intent, PREFERENCES_INTENT)
   }

   fun onClickNewNote(v: View) {
      val intent = Intent(this, NoteCardActivity::class.java)
      intent.putExtra("DataItemMode", DataItemMode.dimInsert)
      startActivityForResult(intent, ENTRY_INTENT)
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == ENTRY_INTENT){
          if (resultCode == Activity.RESULT_OK) {
             data?.let {
                val dataItemModeReturned: DataItemMode = it.getSerializableExtra("DataItemMode") as DataItemMode
                val itemReturned: NotesItem = it.getSerializableExtra("NotesItem") as NotesItem
                Toast.makeText(this, getString(R.string.msgOperationDone, dataItemModeReturned.userString, itemReturned.title), Toast.LENGTH_LONG).show()
                when (dataItemModeReturned){
                    DataItemMode.dimInsert -> itemInserted(itemReturned)
                    DataItemMode.dimEdit -> itemEdited(itemReturned)
                    else -> TODO("not implemented yet")
                }
             }
          }else{
            Toast.makeText(this, getString(R.string.opCancelled), Toast.LENGTH_LONG).show()
         }
      }else{
          if (requestCode == PREFERENCES_INTENT){
              applySettings()
          }
      }
   }
    //endregion

 //region interface AdapterEventListener implementation
    override fun openForEdit(item: NotesItem) {
         val intent = Intent(this, NoteCardActivity::class.java)
         intent.putExtra("DataItemMode", DataItemMode.dimEdit)
         intent.putExtra("DataItem", item)
         startActivityForResult(intent, ENTRY_INTENT)
    }

   override fun itemDeleted(item: NotesItem) {
      db.notesItemDao().delete(item)
      Toast.makeText(this, getString(R.string.msgOperationDone, getString(R.string.opDelete), item.title), Toast.LENGTH_LONG).show()
   }

   override fun itemEdited(item: NotesItem) {
      //db.notesItemDao().update(item)
      val itm =  notesItemList.findByUid(item.uid)!!  //cannot use 'item' for udpate notify, because is's another instance not being contained in the list.
      itm.title= item.title
      itm.text = item.text
      mainItemsGrd.adapter?.notifyItemChanged(notesItemList.indexOf(itm))
    }

   override fun itemInserted(item: NotesItem) {
      notesItemList.add(0, item)
      mainItemsGrd.adapter?.notifyItemInserted(0) //0 - cause inserted at first positition (see above)
      mainItemsGrd.smoothScrollToPosition(0) //as only first was repainted, view is still on previous position. This will scroll to first - newly inserted
   }
 //endregion
}

interface AdapterEventListener{
   fun openForEdit(item: NotesItem)
   fun itemDeleted(item: NotesItem)
   fun itemEdited(item: NotesItem)
   fun itemInserted(item: NotesItem)
}

