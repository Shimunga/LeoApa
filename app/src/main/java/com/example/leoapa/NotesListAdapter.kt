package com.example.leoapa

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_list_card.view.*
import kotlinx.android.synthetic.main.note_list_card.view.removeBtn as removeBtn1

/**
 * Notes list adapter class binds items data to list ui component
 * @param listener reference to list container - activity.
 * @param notesItemList reference to notes list to display in list UI component
 */
class NotesListAdapter(private val listener: AdapterEventListener, private val notesItemList: NotesItemList) : RecyclerView.Adapter<NotesListAdapter.NotesListViewHolder>(){

//region Variables, constants definition
    //Class "holds" items view and where it is in the recycleview
    class NotesListViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //Context used by alert dialog
    var context: Context? = null
   //current item being processed by adapter
    var currentItem: NotesItem? = null
//endregion

 //region Functions, eventhandlers
    /**
     * Inflates layout and creates view holder
     */
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListViewHolder {
      val view= LayoutInflater.from(parent.context).inflate(R.layout.note_list_card, parent, false)
      return NotesListViewHolder(view)
   }

    /**
     * The function gets the cound of items - overridden in order to know that count comes exactly
     * notesItemList
     */
    override fun getItemCount() = notesItemList.size

    /**
     * Fills the UI fields of inflated item view. It is called for every item from the list.
     * @param holder holds the inflated item visualisation
     * @param position current position of list being processed
     */
    override fun onBindViewHolder(holder: NotesListViewHolder, position: Int) {
        currentItem = notesItemList[position]
        context = holder.itemView.context
        holder.itemView.titleTxt.text = currentItem?.title
        holder.itemView.descriptionTxt.text = currentItem?.text

        holder.itemView.removeBtn1.tag = currentItem!!.uid
        holder.itemView.tag = currentItem!!.uid

        //Card's click event handler definition
        holder.itemView.setOnClickListener {
            val tg = (it as CardView).tag
            openForEdit(notesItemList.findByUid(tg as Long)!!) //it.tag as Long)!!
        }

        //Card's delete button event handler definition
        holder.itemView.removeBtn1.setOnClickListener {
            deleteWithConfirmation(notesItemList.findByUid(it.tag as Long)!!)
        }
    }

    /**
     * The function calls delete confirmation dialog and on positive - deletesthe item
     * @param item item to be deleted
    */
    private fun deleteWithConfirmation(item: NotesItem){
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(context?.getString(R.string.msgConfirmationTile))
         .setMessage(context!!.getString(R.string.msgDeleteConfirmation, item?.title))
         .setPositiveButton(context!!.getString(R.string.Yes)) { _, _ -> deleteItem(item)}
         .setNegativeButton(context!!.getString(R.string.No)) { _, _ -> }
         //.setNeutralButton("remind me later") { _, _ -> }
      val dialog = builder.create()
      dialog.show()
   }

    /**
     * The function passes the deletion operation form db via interface to mainActivity, deletes an
     * item from the list and instructs the adapter to update itself
     * @param item item to be deleted
    */
    private fun deleteItem (item: NotesItem) {
        listener.itemDeleted(item)
        notesItemList.remove(item)
        notifyDataSetChanged()
    }

    /**
     * The function passes via interface to mainactivity item editing operation
     */
    private fun openForEdit (item: NotesItem) {
        listener.openForEdit(item)
   }
 //endregion
}