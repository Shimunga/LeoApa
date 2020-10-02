package com.example.leoapa

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_list_card.view.*
import kotlinx.android.synthetic.main.note_list_card.view.removeBtn as removeBtn1

class NotesListAdapter(
   private val listener: AdapterEventListener,
   private val items: MutableList<ShoppingItemForDb>) :
   RecyclerView.Adapter<NotesListAdapter.NotesListViewHolder>() {

   class NotesListViewHolder(view: View) : RecyclerView.ViewHolder(view)

   //Inflate layout and create view holder
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListViewHolder {
      val view =
         LayoutInflater.from(parent.context).inflate(R.layout.note_list_card, parent, false)
      return NotesListViewHolder(
         view
      )
   }

   // Need to return item count
   override fun getItemCount() = items.size

   var context: Context? = null
   var item: ShoppingItemForDb? = null

   override fun onBindViewHolder(holder: NotesListViewHolder, position: Int) {
      item = items[position]
      context = holder.itemView.context
      holder.itemView.titleTxt.text = item?.title
      holder.itemView.descriptionTxt.text = item?.text

      holder.itemView.setOnClickListener {
         Toast.makeText(context, item?.title, Toast.LENGTH_SHORT).show()
      }


      holder.itemView.removeBtn1.setOnClickListener{
         //deleteItem(items.indexOf(item))
         wannaDelete()
      }
   }

   private fun wannaDelete(){
      val builder = AlertDialog.Builder(context!!)
      builder.setTitle("Confirmation")
         .setMessage("Do you really wanna delete the item \"${item?.title}\"?")
         .setPositiveButton("Yes") { _, _ ->
            deleteItem(items.indexOf(item))
         }
         .setNegativeButton("No") { _, _ -> }
         //.setNeutralButton("remind me later") { _, _ -> }
      val dialog = builder.create()
      dialog.show()
   }

   private fun deleteItem (position: Int) {
      listener.deleteClicked(items[position])
      items.removeAt(position)
      notifyDataSetChanged()
   }

}