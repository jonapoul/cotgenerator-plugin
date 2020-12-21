package com.jonapoul.cotgenerator.plugin.ui.status

import android.content.SharedPreferences
import com.jonapoul.cotgenerator.plugin.prefs.Keys
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.cotgenerator.plugin.prefs.getIntFromPair
import timber.log.Timber

internal enum class SortingOrder(private val str: String) {
    ASCENDING("Asc"),
    DESCENDING("Desc");

    companion object {
        fun getFromPrefs(prefs: SharedPreferences): SortingOrder {
            val index = prefs.getIntFromPair(Prefs.SORTING_ORDER)
            Timber.i("Current SortingOrder = $index")
            return values().firstOrNull { it.ordinal == index }
                ?: throw IllegalArgumentException()
        }

        fun saveToPrefs(prefs: SharedPreferences, order: SortingOrder) {
            Timber.i("Putting SortingOrder = ${order.ordinal}")
            prefs.edit()
                .putInt(Keys.SORTING_ORDER, order.ordinal)
                .apply()
        }

        fun atIndex(index: Int) = values()[index]

        fun array(): Array<String> = values().map { it.str }.toTypedArray()
    }
}
