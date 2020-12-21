package com.jonapoul.cotgenerator.plugin.ui.status

import android.content.SharedPreferences
import com.jonapoul.cotgenerator.plugin.prefs.Keys
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.cotgenerator.plugin.prefs.getIntFromPair
import timber.log.Timber

internal enum class SortingType(private val str: String) {
    CALLSIGN("Callsign"),
    TIME("Time"),
    TEAM("Team"),
    ROLE("Role");

    companion object {
        fun getFromPrefs(prefs: SharedPreferences): SortingType {
            val index = prefs.getIntFromPair(Prefs.SORTING_TYPE)
            Timber.i("Current SortingType = $index")
            return values().firstOrNull { it.ordinal == index }
                ?: throw IllegalArgumentException()
        }

        fun saveToPrefs(prefs: SharedPreferences, sortingType: SortingType) {
            Timber.i("Putting SortingType = ${sortingType.ordinal}")
            prefs.edit()
                .putInt(Keys.SORTING_TYPE, sortingType.ordinal)
                .apply()
        }

        fun atIndex(index: Int) = values()[index]

        fun array(): Array<String> = values().map { it.str }.toTypedArray()
    }
}
