package com.jonapoul.cotgenerator.plugin.ui.status

import android.content.SharedPreferences
import com.jonapoul.cotgenerator.plugin.prefs.Keys
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.cotgenerator.plugin.prefs.getIntFromPair
import java.lang.IllegalArgumentException

internal enum class StatusSortingType(private val index: Int) {
    ALPHABET(0),
    TIME(1),
    TEAM(2),
    ROLE(3);

    companion object {
        fun getFromPrefs(prefs: SharedPreferences): StatusSortingType {
            val index = prefs.getIntFromPair(Prefs.MAP_ITEM_SORTING)
            return values().firstOrNull { it.index == index }
                ?: throw IllegalArgumentException()
        }

        fun saveToPrefs(prefs: SharedPreferences, sortingType: StatusSortingType) {
            prefs.edit()
                .putInt(Keys.MAP_ITEM_SORTING, sortingType.index)
                .apply()
        }
    }
}
