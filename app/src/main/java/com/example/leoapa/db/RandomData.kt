package com.example.leoapa

import android.annotation.SuppressLint
import java.util.Random

/**
 * Singleton class for generating data for testing purposes
 */
object RandomData {
    private val random = Random()

    private const val LOREM =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
                "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat " +
                "nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
                "qui officia deserunt mollit anim id est laborum."

    val randomLorem
        get() = LOREM.take(random.nextInt(LOREM.length))

    val randomTitle
        @SuppressLint("DefaultLocale")
        get() = with(LOREM.split(" ")) {
            this[random.nextInt(size)].capitalize()
        }
}