package com.jonapoul.cotgenerator.plugin.prefs

object Keys {
    private fun get(key: String) = "cotgenerator.${key}"

    /* Status tab sorting settings */
    val SORTING_TYPE = get("sorting_type")
    val SORTING_ORDER = get("sorting_order")

    /* CoT settings */
    val USE_RANDOM_CALLSIGNS = get("use_random_callsigns")
    val BASE_CALLSIGN = get("base_callsign")
    val USE_INDEXED_CALLSIGN = get("use_indexed_callsign")
    val USE_RANDOM_TEAM_COLOURS = get("use_random_team_colours")
    val TEAM_COLOUR = get("team_colour")
    val USE_RANDOM_ROLES = get("use_random_roles")
    val ROLE = get("role")
    val ICON_COUNT = get("icon_count")
    val STALE_TIMER = get("stale_timer")

    /* Location settings */
    val FOLLOW_SELF_MARKER = get("follow_self_marker")
    val CENTRE_LATITUDE = get("centre_latitude")
    val CENTRE_LONGITUDE = get("centre_longitude")
    val STAY_AT_GROUND_LEVEL = get("stay_at_ground_level")
    val CENTRE_ALTITUDE = get("centre_altitude")
    val RADIAL_DISTRIBUTION = get("radial_distribution")
    val MOVEMENT_SPEED = get("movement_speed")

    /* Transmission settings */
    val UPDATE_FREQUENCY_PER_ICON = get("update_frequency_per_icon")
}
