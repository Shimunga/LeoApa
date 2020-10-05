package com.example.leoapa

import android.content.Context
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog

class UIutils {
    companion object{
        public fun getSpinnerIndexByString(spinner: Spinner, myString: String): Int {
            var index = 0
            for (i in 0 until spinner.count) {
                if (spinner.getItemAtPosition(i) == myString) {
                    index = i
                    break
                }
            }
            return index
        }

        public fun showInfo(context: Context, message: String){
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.msgInformationTile))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.Ok)) { _, _ -> }
            val dialog = builder.create()
            dialog.show()
        }
    }

}