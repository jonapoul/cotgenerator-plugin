package com.jonapoul.cotgenerator.plugin.prefs

object Keys {
    private fun get(key: String) = "cotgenerator.${key}"

    const val DEVICE_UID = "bestDeviceUID" // set in LocationMapComponent::_determineBestDeviceUID
    const val COORDINATE_DISPLAY_FORMAT = "coord_display_pref"

    /* Centre Point settings */
    val FOLLOW_SELF_MARKER = get("follow_self_marker")
    val DRAW_CIRCLE = get("draw_circle")
    val STAY_AT_GROUND_LEVEL = get("stay_at_ground_level")
    val CENTRE_LATITUDE = get("centre_latitude")
    val CENTRE_LONGITUDE = get("centre_longitude")
    val CENTRE_ALTITUDE = get("centre_altitude")

    /* Callsign settings */
    val USE_RANDOM_CALLSIGNS = get("use_random_callsigns")
    val USE_INDEXED_CALLSIGN = get("use_indexed_callsign")
    val BASE_CALLSIGN = get("base_callsign")

    /* CoT Data settings */
    val USE_RANDOM_TEAMS = get("use_random_team_colours")
    val TEAM = get("team_colour")
    val USE_RANDOM_ROLES = get("use_random_roles")
    val ROLE = get("role")
    val COUNT = get("icon_count")
    val STALE_TIMER = get("stale_timer")
    val RADIUS = get("radial_distribution")
    val SPEED = get("movement_speed")
    val PERIOD = get("update_period")

    /* View visibility settings */
    val IS_VISIBLE_CENTRE_POINT = get("is_visible_centre_point")
    val IS_VISIBLE_CALLSIGN = get("is_visible_callsign")
    val IS_VISIBLE_COT_DATA = get("is_visible_cot_data")
}
