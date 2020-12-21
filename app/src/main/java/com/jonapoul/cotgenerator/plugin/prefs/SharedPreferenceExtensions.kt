package com.jonapoul.cotgenerator.plugin.prefs

import android.content.SharedPreferences

/** Some QoL utility methods for pulling values/defaults from the [SharedPreferences] */
fun SharedPreferences.parseIntFromPair(pref: Prefs.Pair<String>): Int {
    return this.getString(pref.key, pref.default)!!.toInt()
}

fun SharedPreferences.getIntFromPair(pref: Prefs.Pair<Int>): Int {
    return this.getInt(pref.key, pref.default)
}

fun SharedPreferences.getLongFromPair(pref: Prefs.Pair<Long>): Long {
    return this.getLong(pref.key, pref.default)
}

fun SharedPreferences.getStringFromPair(pref: Prefs.Pair<String>): String {
    return this.getString(pref.key, pref.default)!!
}

fun SharedPreferences.getBooleanFromPair(pref: Prefs.Pair<Boolean>): Boolean {
    return this.getBoolean(pref.key, pref.default)
}
