package com.example.leoapa

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

   companion object {
      const val ENTRY_INTENT = 100
      const val ENTRY_RESULT = "EntryResult"
   }

   private val shoppingItemsForDb = mutableListOf<ShoppingItemForDb>()

   private val db get() = Database.getInstance(this)

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      //load from database
      shoppingItemsForDb.addAll(db.shoppingItemForDbDao().getAll())

      //setup adapter
      val adapter =
         NotesListAdapter(
            this,
            shoppingItemsForDb
         )
      mainItemsGrd.adapter = adapter

      //shoppingItems.addAll(0, RandomData.items)

      staggLinearSwitch.setOnCheckedChangeListener { _, isChecked -> switchLayouts(isChecked) }
   }

   fun onClickAddBtn() {
      val name = itemEd.text.toString()
      val itemNew = ShoppingItemForDb(
         name,
         RandomData.randomLorem
      )
      shoppingItemsForDb.add(0, itemNew) //RandomData.randomItem

      //repaints all elements
      //mainItemsGrd.adapter?.notifyDataSetChanged()

      //repaints only inserted at the position specified
      mainItemsGrd.adapter?.notifyItemInserted(0) //0 - cause inserted at frst postition (see above)
      mainItemsGrd.smoothScrollToPosition(0) //as only first was repainted, view is still on previous position. This will scroll to first - newly inserted
      itemEd.setText(RandomData.randomTitle)

      //save to db
      itemNew.uid = db.shoppingItemForDbDao().insertAll(itemNew).first()
   }

   fun onClickSortBtn() {
      //shoppingItems.sortedBy{view.transitionName}
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

   override fun deleteClicked(item: ShoppingItemForDb) {
      db.shoppingItemForDbDao().delete(item)
   }

   fun onClickNewNote() {
      //val intent = Intent(this, NoteCardActivity::class.java)
      //startActivity(intent)
      val intent = Intent(this, NoteCardActivity::class.java)
      startActivityForResult(intent, ENTRY_INTENT)
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == ENTRY_INTENT /*&& resultCode == Activity.RESULT_OK*/) {
         data?.let {
            Toast.makeText(this, "Note entry form result: ${data.getStringExtra(ENTRY_RESULT)}", Toast.LENGTH_LONG).show()
         }
      }
   }

}

interface AdapterEventListener{
   fun deleteClicked(item: ShoppingItemForDb)
}

