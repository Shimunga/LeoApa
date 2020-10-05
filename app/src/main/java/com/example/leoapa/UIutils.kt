package com.example.leoapa

import android.widget.Spinner

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
    }

}