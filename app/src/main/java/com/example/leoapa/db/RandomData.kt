package com.example.leoapa

import android.annotation.SuppressLint
import java.util.Random

sealed class NoteItem{
    var title: String =""
    constructor(title: String){
        this.title = title
    }
}
data class NoteItemText(val _title: String, val text: String) : NoteItem(_title)
data class NoteItemImage(val _title: String, val uri: String) : NoteItem(_title)

object RandomData {

    private val random = Random()

    private const val LOREM =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
                "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat " +
                "nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
                "qui officia deserunt mollit anim id est laborum."

    private const val LIST_SIZE = 10

    val randomLorem
        get() = LOREM.take(random.nextInt(LOREM.length))

    val randomTitle
        @SuppressLint("DefaultLocale")
        get() = with(LOREM.split(" ")) {
            this[random.nextInt(size)].capitalize()
        }

    private val uris = listOf(
        "https://p.kindpng.com/picc/s/72-722801_bread-roll-png-roll-of-bread-png-transparent.png",
        "https://atlas-content-cdn.pixelsquid.com/stock-images/bowl-with-eggs-egg-xwVrGL4-600.jpg",
        "https://atlas-content-cdn.pixelsquid.com/stock-images/glass-of-milk-2MEn1r7-600.jpg",
        "https://www.netclipart.com/pp/m/76-768140_freshly-fresh-potatoes-fresh-potatoes-png.png"
    )

    private val randomUri
        get() = uris[random.nextInt(uris.size)]

    private val randomSize
        get() = random.nextInt(LIST_SIZE)

    val randomItem
        get() = if (random.nextBoolean()) textItem else imageItem
        //get() = imageItem

    val items
        get() = MutableList(randomSize) { randomItem }

    val textItem get() = NoteItemText(randomTitle, randomLorem)

    val imageItem get() = NoteItemImage(randomTitle,randomUri)

}