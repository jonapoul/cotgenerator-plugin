package com.jonapoul.cotgenerator.plugin.prefs

import com.jonapoul.sharedprefs.PrefPair

object Prefs {
    val SORTING_TYPE = PrefPair(Keys.SORTING_TYPE, Defaults.SORTING_TYPE)
    val SORTING_ORDER = PrefPair(Keys.SORTING_ORDER, Defaults.SORTING_ORDER)
}
