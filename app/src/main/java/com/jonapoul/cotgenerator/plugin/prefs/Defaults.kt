package com.jonapoul.cotgenerator.plugin.prefs

import com.jonapoul.cotgenerator.plugin.cot.CotRole
import com.jonapoul.cotgenerator.plugin.cot.CotTeam

object Defaults {
    const val DEVICE_UID = "DEVICE-UID"

    /* CoT settings */
    const val USE_RANDOM_CALLSIGNS = true
    const val BASE_CALLSIGN = "GENERATED"
    const val USE_INDEXED_CALLSIGN = true
    const val USE_RANDOM_TEAM_COLOURS = true
    val TEAM_COLOUR = CotTeam.CYAN.toString()
    const val USE_RANDOM_ROLES = true
    val ROLE = CotRole.TEAM_MEMBER.toString()
    const val ICON_COUNT = "10"
    const val STALE_TIMER = "5"

    /* Location settings */
    const val FOLLOW_SELF_MARKER = true
    const val CENTRE_LATITUDE = "51.238956"
    const val CENTRE_LONGITUDE = "-0.672416"
    const val STAY_AT_GROUND_LEVEL = true
    const val CENTRE_ALTITUDE = "0"
    const val RADIAL_DISTRIBUTION = "200"
    const val MOVEMENT_SPEED = "5"

    /* Transmission settings */
    const val UPDATE_FREQUENCY_PER_ICON = "5"
}
