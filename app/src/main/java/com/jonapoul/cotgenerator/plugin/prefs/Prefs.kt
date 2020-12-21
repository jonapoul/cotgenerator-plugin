package com.jonapoul.cotgenerator.plugin.prefs

object Prefs {
    data class Pair<T>(val key: String, val default: T)

    val MAP_ITEM_SORTING = Pair(Keys.MAP_ITEM_SORTING, Defaults.MAP_ITEM_SORTING)
}
