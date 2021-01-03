package com.jonapoul.cotgenerator.plugin.prefs

import com.jonapoul.sharedprefs.PrefPair

object Prefs {
    /* Set by ATAK itself */
    val DEVICE_UID = PrefPair(Keys.DEVICE_UID, Defaults.DEVICE_UID)
    val COORDINATE_DISPLAY_FORMAT = PrefPair(Keys.COORDINATE_DISPLAY_FORMAT, Defaults.COORDINATE_DISPLAY_FORMAT)

    /* Centre Point settings */
    val FOLLOW_SELF_MARKER = PrefPair(Keys.FOLLOW_SELF_MARKER, Defaults.FOLLOW_SELF_MARKER)
    val DRAW_CIRCLE = PrefPair(Keys.DRAW_CIRCLE, Defaults.DRAW_CIRCLE)
    val STAY_AT_GROUND_LEVEL = PrefPair(Keys.STAY_AT_GROUND_LEVEL, Defaults.STAY_AT_GROUND_LEVEL)
    val CENTRE_LATITUDE = PrefPair(Keys.CENTRE_LATITUDE, Defaults.CENTRE_LATITUDE)
    val CENTRE_LONGITUDE = PrefPair(Keys.CENTRE_LONGITUDE, Defaults.CENTRE_LONGITUDE)
    val CENTRE_ALTITUDE = PrefPair(Keys.CENTRE_ALTITUDE, Defaults.CENTRE_ALTITUDE)

    /* Callsign settings */
    val USE_RANDOM_CALLSIGNS = PrefPair(Keys.USE_RANDOM_CALLSIGNS, Defaults.USE_RANDOM_CALLSIGNS)
    val USE_INDEXED_CALLSIGN = PrefPair(Keys.USE_INDEXED_CALLSIGN, Defaults.USE_INDEXED_CALLSIGN)
    val BASE_CALLSIGN = PrefPair(Keys.BASE_CALLSIGN, Defaults.BASE_CALLSIGN)

    /* CoT Data settings */
    val USE_RANDOM_TEAMS = PrefPair(Keys.USE_RANDOM_TEAMS, Defaults.USE_RANDOM_TEAMS)
    val TEAM = PrefPair(Keys.TEAM, Defaults.TEAM)
    val USE_RANDOM_ROLES = PrefPair(Keys.USE_RANDOM_ROLES, Defaults.USE_RANDOM_ROLES)
    val ROLE = PrefPair(Keys.ROLE, Defaults.ROLE)
    val COUNT = PrefPair(Keys.COUNT, Defaults.COUNT)
    val STALE_TIMER = PrefPair(Keys.STALE_TIMER, Defaults.STALE_TIMER)
    val RADIUS = PrefPair(Keys.RADIUS, Defaults.RADIUS)
    val SPEED = PrefPair(Keys.SPEED, Defaults.SPEED)
    val PERIOD = PrefPair(Keys.PERIOD, Defaults.PERIOD)

    /* View visibility settings */
    val IS_VISIBLE_CENTRE_POINT = PrefPair(Keys.IS_VISIBLE_CENTRE_POINT, Defaults.IS_VISIBLE_CENTRE_POINT)
    val IS_VISIBLE_CALLSIGN = PrefPair(Keys.IS_VISIBLE_CALLSIGN, Defaults.IS_VISIBLE_CALLSIGN)
    val IS_VISIBLE_COT_DATA = PrefPair(Keys.IS_VISIBLE_COT_DATA, Defaults.IS_VISIBLE_COT_DATA)
}
