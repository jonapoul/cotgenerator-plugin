package com.jonapoul.cotgenerator.plugin.prefs

internal fun validateInt(str: String, min: Int? = null, max: Int? = null): Boolean {
    return try {
        isInRange(str.toInt(), min, max)
    } catch (e: NumberFormatException) {
        false
    }
}

internal fun validateDouble(str: String, min: Double? = null, max: Double? = null): Boolean {
    return try {
        isInRange(str.toDouble(), min, max)
    } catch (e: NumberFormatException) {
        false
    }
}

internal fun validateCallsign(callsign: String): Boolean {
    /* These characters break TAK parsing when using XML. */
    val disallowedCharacters = arrayOf(
        '<', '>', '&', '\"'
    )
    return disallowedCharacters.none { callsign.contains(it) }
}

private fun <T : Comparable<T>> isInRange(value: T, min: T?, max: T?): Boolean {
    if (min != null && value < min) return false
    if (max != null && value > max) return false
    return true
}