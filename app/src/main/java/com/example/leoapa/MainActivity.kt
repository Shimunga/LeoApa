package com.example.leoapa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.leobase.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Class contains notes list, adapter and functions for working with notes.
 */
class MainActivity : BaseActivity(), AdapterEventListener {
 //region Variables, constants definition
   companion object {
      const val ENTRY_INTENT = 100 //intent constant for calling entry activity
      const val PREFERENCES_INTENT = 110 //intent constant for calling settings activity
   }
   private val notesItemList = NotesItemList()
   private val db get() = Database.getInstance(this)
 //endregion

 //region Functions, eventhandlers
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      //load from database
      notesItemList.addAll(db.notesItemDao().getAll())

      //setup adapter
      val adapter =
         NotesListAdapter(
            this,
            notesItemList
         )
     mainItemsGrd.adapter = adapter


     applySettings()
    }

    /**
     * Function retrieves settings and applies to acitvity, eg. layout style
     */
    private fun applySettings(){
        switchLayouts(settings?.retrieveParamBool(AppParams.prmLayoutMode)!!)
        //setLocale(settings?.retrieveParamString(AppParams.prmLang)!!)
     }

    /**
     * Function switches list layout from staggered to linear
     * @param isLinearStaggered true makes it linear, false - staggered
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

    /**
     * Function opens settings activity
     */
    fun onClickOpenConfigBtn(v: View) {
       val intent = Intent(this, SettingsActivity::class.java)
       startActivityForResult(intent, PREFERENCES_INTENT)
   }

    /**
     * Function opens note edit form for data input with generated data
     */
    fun onClickNewNote(v: View) {
      val intent = Intent(this, NoteCardActivity::class.java)
      intent.putExtra("DataItemMode", DataItemMode.dimInsert)
      startActivityForResult(intent, ENTRY_INTENT)
   }

    /**
     * Activity result event handler to process returns from note and settings activities
     */
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

 //region Interface AdapterEventListener implementation
    /**
     * Function opens note activity for editing.
     * @param item is passed in order to know which item should be edited in the note's activity
     */
    override fun openForEdit(item: NotesItem) {
         val intent = Intent(this, NoteCardActivity::class.java)
         intent.putExtra("DataItemMode", DataItemMode.dimEdit)
         intent.putExtra("DataItem", item)
         startActivityForResult(intent, ENTRY_INTENT)
    }

    /**
     * Function delete the item from database and UI list
     * @param item is passed in order to know which item should be deleted
     */
    override fun itemDeleted(item: NotesItem) {
      db.notesItemDao().delete(item)
      Toast.makeText(this, getString(R.string.msgOperationDone, getString(R.string.opDelete), item.title), Toast.LENGTH_LONG).show()
    }

    /**
     * Function updates item in the list and notifies adapter to update UI. It happens after item
     * was being updated in the db.
     * @param item is passed in order to know which item to be updated.
     */
    override fun itemEdited(item: NotesItem) {
      val itm =  notesItemList.findByUid(item.uid)!!  //cannot use 'item' for udpate notify, because is's another instance not being contained in the list.
      itm.title= item.title
      itm.text = item.text
      mainItemsGrd.adapter?.notifyItemChanged(notesItemList.indexOf(itm))
    }

    /**
     * Function inserts item in the list and notifies adapter to update UI. It happens after item
     * was being inserted in the db.
     * @param item is passed in order to know which item to be inserted.
     */
    override fun itemInserted(item: NotesItem) {
      notesItemList.add(0, item)
      mainItemsGrd.adapter?.notifyItemInserted(0) //0 - cause inserted at first positition (see above)
      mainItemsGrd.smoothScrollToPosition(0) //as only first was repainted, view is still on previous position. This will scroll to first - newly inserted
    }
 //endregion
}

/**
 * Interface defines functions for manipulation with notes item
 */
interface AdapterEventListener{
   fun openForEdit(item: NotesItem)
   fun itemDeleted(item: NotesItem)
   fun itemEdited(item: NotesItem)
   fun itemInserted(item: NotesItem)
}

