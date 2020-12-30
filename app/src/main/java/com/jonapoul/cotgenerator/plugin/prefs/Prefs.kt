package com.jonapoul.cotgenerator.plugin.prefs

import com.jonapoul.sharedprefs.PrefPair

object Prefs {
    /* Set by ATAK itself */
    val DEVICE_UID = PrefPair(Keys.DEVICE_UID, Defaults.DEVICE_UID)
    val COORDINATE_DISPLAY_FORMAT = PrefPair(Keys.COORDINATE_DISPLAY_FORMAT, Defaults.COORDINATE_DISPLAY_FORMAT)

    /* CoT settings */
    val USE_RANDOM_CALLSIGNS = PrefPair(Keys.USE_RANDOM_CALLSIGNS, Defaults.USE_RANDOM_CALLSIGNS)
    val BASE_CALLSIGN = PrefPair(Keys.BASE_CALLSIGN, Defaults.BASE_CALLSIGN)
    val USE_INDEXED_CALLSIGN = PrefPair(Keys.USE_INDEXED_CALLSIGN, Defaults.USE_INDEXED_CALLSIGN)
    val USE_RANDOM_TEAM_COLOURS = PrefPair(Keys.USE_RANDOM_TEAM_COLOURS, Defaults.USE_RANDOM_TEAM_COLOURS)
    val TEAM_COLOUR = PrefPair(Keys.TEAM_COLOUR, Defaults.TEAM_COLOUR)
    val USE_RANDOM_ROLES = PrefPair(Keys.USE_RANDOM_ROLES, Defaults.USE_RANDOM_ROLES)
    val ROLE = PrefPair(Keys.ROLE, Defaults.ROLE)
    val ICON_COUNT = PrefPair(Keys.ICON_COUNT, Defaults.ICON_COUNT)
    val STALE_TIMER = PrefPair(Keys.STALE_TIMER, Defaults.STALE_TIMER)

    /* Location settings */
    val FOLLOW_SELF_MARKER = PrefPair(Keys.FOLLOW_SELF_MARKER, Defaults.FOLLOW_SELF_MARKER)
    val CENTRE_LATITUDE = PrefPair(Keys.CENTRE_LATITUDE, Defaults.CENTRE_LATITUDE)
    val CENTRE_LONGITUDE = PrefPair(Keys.CENTRE_LONGITUDE, Defaults.CENTRE_LONGITUDE)
    val STAY_AT_GROUND_LEVEL = PrefPair(Keys.STAY_AT_GROUND_LEVEL, Defaults.STAY_AT_GROUND_LEVEL)
    val CENTRE_ALTITUDE = PrefPair(Keys.CENTRE_ALTITUDE, Defaults.CENTRE_ALTITUDE)
    val RADIAL_DISTRIBUTION = PrefPair(Keys.RADIAL_DISTRIBUTION, Defaults.RADIAL_DISTRIBUTION)
    val MOVEMENT_SPEED = PrefPair(Keys.MOVEMENT_SPEED, Defaults.MOVEMENT_SPEED)

    /* Transmission settings */
    val UPDATE_PERIOD = PrefPair(Keys.UPDATE_PERIOD, Defaults.UPDATE_PERIOD)
}
