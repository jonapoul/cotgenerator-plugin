package com.jonapoul.cotgenerator.plugin.prefs

object Prefs {
    data class Pair<T>(val key: String, val default: T)

    val SORTING_TYPE = Pair(Keys.SORTING_TYPE, Defaults.SORTING_TYPE)
    val SORTING_ORDER = Pair(Keys.SORTING_ORDER, Defaults.SORTING_ORDER)
}
