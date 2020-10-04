package com.example.leoapa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterEventListener {
 //region variables, constants definition
   companion object {
      const val ENTRY_INTENT = 100
   }
   private val notesItemList = NotesItemList()//mutableListOf<NotesItem>()
   private val db get() = Database.getInstance(this)
 //endregion

 //region functions, eventhandlers
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

      staggLinearSwitch.setOnCheckedChangeListener { _, isChecked -> switchLayouts(isChecked) }
   }

   private fun switchLayouts(/*buttonView: CompoundButton, */isChecked: Boolean) {
      Log.v("Switch State=", "" + isChecked)

      if (isChecked) {
         mainItemsGrd.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
      }else {
         mainItemsGrd.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
         (mainItemsGrd.layoutManager as StaggeredGridLayoutManager).setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE)
      }
   }

   fun onClickSortBtn(v: View) {
      //shoppingItems.sortedBy{view.transitionName}
   }

   fun onClickNewNote(v: View) {
      //val intent = Intent(this, NoteCardActivity::class.java)
      //startActivity(intent)
      val intent = Intent(this, NoteCardActivity::class.java)
      intent.putExtra("DataItemMode", DataItemMode.dimInsert)
      startActivityForResult(intent, ENTRY_INTENT)
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == ENTRY_INTENT && resultCode == Activity.RESULT_OK) {
         data?.let {
            val dataItemModeReturned: DataItemMode = it.getSerializableExtra("DataItemMode") as DataItemMode
            val itemReturned: NotesItem = it.getSerializableExtra("NotesItem") as NotesItem
            Toast.makeText(this, "Operation ${dataItemModeReturned.userString} done with item: ${itemReturned.title}", Toast.LENGTH_LONG).show()
            when (dataItemModeReturned){
               DataItemMode.dimInsert -> itemInserted(itemReturned)
               else -> TODO("not implemented yet")
            }
         }
      }
      else{
         if (requestCode == ENTRY_INTENT && resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "Operation cancelled", Toast.LENGTH_LONG).show()
         }
      }
   }
 //endregion

 //region interface AdapterEventListener implementation
   override fun itemDeleted(item: NotesItem) {
      db.notesItemDao().delete(item)
      Toast.makeText(this, "Operation Delete done with item: ${item.title}", Toast.LENGTH_LONG).show()
   }

   override fun itemChanged(item: NotesItem) {
      //db.notesItemDao().update(item)
   }

   override fun itemInserted(item: NotesItem) {
      notesItemList.add(0, item)
      mainItemsGrd.adapter?.notifyItemInserted(0) //0 - cause inserted at first positition (see above)
      mainItemsGrd.smoothScrollToPosition(0) //as only first was repainted, view is still on previous position. This will scroll to first - newly inserted
   }
 //endregion
}

interface AdapterEventListener{
   fun itemDeleted(item: NotesItem)
   fun itemChanged(item: NotesItem)
   fun itemInserted(item: NotesItem)
}

