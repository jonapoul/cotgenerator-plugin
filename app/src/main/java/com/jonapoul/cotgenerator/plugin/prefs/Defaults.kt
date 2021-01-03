package com.jonapoul.cotgenerator.plugin.prefs

import com.jonapoul.cotgenerator.plugin.cot.CotRole
import com.jonapoul.cotgenerator.plugin.cot.CotTeam

object Defaults {
    const val DEVICE_UID = "DEVICE-UID"
    val COORDINATE_DISPLAY_FORMAT = CoordinateFormat.MGRS.toString()

    /* Centre Point settings */
    const val FOLLOW_SELF_MARKER = true
    const val DRAW_CIRCLE = false
    const val STAY_AT_GROUND_LEVEL = true
    const val CENTRE_LATITUDE = "51.238956"
    const val CENTRE_LONGITUDE = "-0.672416"
    const val CENTRE_ALTITUDE = "0"

    /* Callsign settings */
    const val USE_RANDOM_CALLSIGNS = true
    const val USE_INDEXED_CALLSIGN = true
    const val BASE_CALLSIGN = "CALLSIGN"

    /* CoT Data settings */
    const val USE_RANDOM_TEAM_COLOURS = true
    val TEAM_COLOUR = CotTeam.CYAN.toString()
    const val USE_RANDOM_ROLES = true
    val ROLE = CotRole.TEAM_MEMBER.toString()
    const val ICON_COUNT = "10"
    const val STALE_TIMER = "5"
    const val RADIAL_DISTRIBUTION = "200"
    const val MOVEMENT_SPEED = "5"
    const val UPDATE_PERIOD = "5"
}
