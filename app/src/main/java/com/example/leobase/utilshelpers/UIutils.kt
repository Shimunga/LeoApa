package com.example.leobase

import android.content.Context
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.example.leoapa.R

/**
 * The class contains static UI controls helper functions
 */
class UIutils {
    companion object{
        /**
         * The function gets the index by string
         * @param spinner control to be iterated through
         * @param myString string to be searched for
         * @return index of the string found in the items list of the control. -1 returned if not found
         */
        fun getSpinnerIndexByString(spinner: Spinner, myString: String): Int {
            var index = -1
            for (i in 0 until spinner.count) {
                if (spinner.getItemAtPosition(i) == myString) {
                    index = i
                    break
                }
            }
            return index
        }

        /**
         * Informatin dialog are shown
         * @param message message text will be displayed in the alert
         */
        fun showInfo(context: Context, message: String){
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.msgInformationTile))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.Ok)) { _, _ -> }
            val dialog = builder.create()
            dialog.show()
        }

    }

}