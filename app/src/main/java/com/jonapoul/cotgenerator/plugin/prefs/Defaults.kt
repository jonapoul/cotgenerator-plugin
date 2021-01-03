package com.jonapoul.cotgenerator.plugin.prefs

import com.atakmap.coremap.conversions.CoordinateFormat
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
    const val USE_RANDOM_TEAMS = true
    val TEAM = CotTeam.CYAN.toString()
    const val USE_RANDOM_ROLES = true
    val ROLE = CotRole.TEAM_MEMBER.toString()
    const val COUNT = 10
    const val STALE_TIMER = 5
    const val RADIUS = 200
    const val SPEED = 5
    const val PERIOD = 5

    /* View visibility settings */
    const val IS_VISIBLE_CENTRE_POINT = true
    const val IS_VISIBLE_CALLSIGN = true
    const val IS_VISIBLE_COT_DATA = true
}
