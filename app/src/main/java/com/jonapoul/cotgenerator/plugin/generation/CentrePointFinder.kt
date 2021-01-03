package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.android.maps.MapView
import com.atakmap.android.util.ATAKUtilities
import com.atakmap.coremap.maps.coords.GeoPoint
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.parseDoubleFromPair

object CentrePointFinder {
    private lateinit var selfPoint: GeoPoint

    fun get(mapView: MapView, prefs: SharedPreferences): GeoPoint {
        selfPoint = ATAKUtilities.findSelfUnplaced(mapView).point
        return GeoPoint(
            getLatitude(prefs),
            getLongitude(prefs),
            getAltitude(prefs)
        )
    }

    private fun getLatitude(prefs: SharedPreferences): Degrees {
        return if (prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)) {
            selfPoint.latitude
        } else {
            prefs.parseDoubleFromPair(Prefs.CENTRE_LATITUDE)
        }
    }

    private fun getLongitude(prefs: SharedPreferences): Degrees {
        return if (prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)) {
            selfPoint.longitude
        } else {
            prefs.parseDoubleFromPair(Prefs.CENTRE_LONGITUDE)
        }
    }

    private fun getAltitude(prefs: SharedPreferences): Double {
        return if (prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)) {
            selfPoint.altitude
        } else {
            prefs.parseDoubleFromPair(Prefs.CENTRE_ALTITUDE)
        }
    }
}
