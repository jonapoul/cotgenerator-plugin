package com.jonapoul.cotgenerator.plugin.prefs

object Keys {
    private const val PREFIX = "com.jonapoul.cotgenerator.plugin."

    val SORTING_TYPE = get("sorting_type")
    val SORTING_ORDER = get("sorting_order")

    private fun get(key: String) = "${PREFIX}${key}"
}
