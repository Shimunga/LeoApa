package com.example.leoapa

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_list_card.view.*
import kotlinx.android.synthetic.main.note_list_card.view.removeBtn as removeBtn1

class NotesListAdapter(private val listener: AdapterEventListener, private val notesItemList: NotesItemList) : RecyclerView.Adapter<NotesListAdapter.NotesListViewHolder>()
{
 //region variables, constants definition
   class NotesListViewHolder(view: View) : RecyclerView.ViewHolder(view)
   var context: Context? = null
   var currentItem: NotesItem? = null
 //endregion

 //region functions, eventhandlers
   //Inflate layout and create view holder
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListViewHolder {
      val view= LayoutInflater.from(parent.context).inflate(R.layout.note_list_card, parent, false)
      return NotesListViewHolder(view)
   }

   // Need to return item count
   override fun getItemCount() = notesItemList.size

   override fun onBindViewHolder(holder: NotesListViewHolder, position: Int) {
      currentItem = notesItemList[position]
      context = holder.itemView.context
      holder.itemView.titleTxt.text = currentItem?.title
      holder.itemView.descriptionTxt.text = currentItem?.text
      holder.itemView.removeBtn1.tag = currentItem!!.uid
      holder.itemView.tag = currentItem!!.uid

      holder.itemView.setOnClickListener {
         val tg = (it as CardView).tag
         openForEdit(notesItemList.findByUid(tg as Long)!!) //it.tag as Long)!!
      }

      holder.itemView.removeBtn1.setOnClickListener{
         deleteWithConfirmation(notesItemList.findByUid(it.tag as Long)!!)
      }
   }

   private fun deleteWithConfirmation(item: NotesItem){
      val builder = AlertDialog.Builder(context!!)
      builder.setTitle(context?.getString(R.string.msgConfirmationTile))
         .setMessage("Do you really wanna delete the item \"${item?.title}\"?")
         .setPositiveButton("Yes") { _, _ -> deleteItem(item)}
         .setNegativeButton("No") { _, _ -> }
         //.setNeutralButton("remind me later") { _, _ -> }
      val dialog = builder.create()
      dialog.show()
   }

   private fun deleteItem (item: NotesItem) {
      listener.itemDeleted(item)
      notesItemList.remove(item)
      notifyDataSetChanged()
   }

   private fun openForEdit (item: NotesItem) {
       listener.openForEdit(item)
       //listener.itemEdited(item)
   }

 //endregion
}