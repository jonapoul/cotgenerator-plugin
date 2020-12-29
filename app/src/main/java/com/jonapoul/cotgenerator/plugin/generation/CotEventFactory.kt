package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.android.cot.detail.PrecisionLocationHandler
import com.atakmap.android.maps.MapView
import com.atakmap.android.maps.Marker
import com.atakmap.android.util.ATAKUtilities
import com.atakmap.coremap.cot.event.CotEvent
import com.atakmap.coremap.cot.event.CotPoint
import com.atakmap.map.elevation.ElevationData
import com.atakmap.map.elevation.ElevationManager
import com.jonapoul.cotgenerator.plugin.generation.streams.DoubleRandomStream
import com.jonapoul.cotgenerator.plugin.generation.streams.IRandomStream
import com.jonapoul.cotgenerator.plugin.generation.streams.IntRandomStream
import com.jonapoul.cotgenerator.plugin.generation.streams.RadialDistanceRandomStream
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.getStringFromPair
import com.jonapoul.sharedprefs.parseDoubleFromPair
import com.jonapoul.sharedprefs.parseIntFromPair
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

internal class CotEventFactory(
    private val mapView: MapView,
    private val prefs: SharedPreferences,
    private val callsigns: List<String>
) {

    private val random = Random(System.currentTimeMillis())
    private val precisionLocationHandler = PrecisionLocationHandler.getInstance()

    private var useRandomCallsigns: Boolean = false
    private var useIndexedCallsigns: Boolean = false
    private var useRandomTeams: Boolean = false
    private var useRandomRoles: Boolean = false
    private var iconCount: Int = 1
    private var distributionRadius: Double = 0.0
    private var followGps: Boolean = false
    private var centreLat: Double = 0.0
    private var centreLon: Double = 0.0
    private var stayAtGroundLevel: Boolean = false
    private var centreAlt: Double = 0.0
    private var staleTimer: Int = 0
    private var movementSpeed: Double = 0.0
    private var travelDistance: Double = 0.0

    private lateinit var selfMarker: Marker
    private lateinit var distributionCentre: CotPoint
    private var data = ArrayList<CotEventWrapper>()

    fun generate(): List<CotEvent> {
        Timber.i("generate")
        return if (data.isEmpty()) initialise() else update()
    }

    private fun initialise(): List<CotEvent> {
        Timber.i("initialise")
        grabValuesFromPreferences()
        data = ArrayList()
        updateDistributionCentre()
        val distanceItr = weightedRadialIterator()
        val courseItr = doubleIterator(min = 0.0, max = 360.0)
        val altitudeItr = doubleIterator(
            min = centreAlt - distributionRadius,
            max = centreAlt + distributionRadius
        )
        val deviceUid = prefs.getStringFromPair(Prefs.DEVICE_UID)

        for (i in 0 until iconCount) {
            val event = CotEventWrapper().also {
                /* Initially place it on the distribution centre */
                it.setPoint(
                    point = distributionCentre,
                    altitude = initialiseAltitude(distributionCentre, altitudeItr)
                )
                /* Determine an initial offset to place each icon at a different location */
                it.generateAndUpdateOffset(distanceItr, courseItr)
                /* Set all other values on the event */
                it.initialise(
                    prefs = prefs,
                    uid = "${deviceUid}_$i",
                    callsign = callsigns[i],
                    selfMarker = selfMarker,
                    staleTimer = staleTimer,
                    movementSpeed = movementSpeed,
                    precisionLocationHandler = precisionLocationHandler
                )
            }
            data.add(event)
        }
        return getCotList()
    }

    private fun update(): List<CotEvent> {
        Timber.i("update")
        updateDistributionCentre()
        val courseItr = doubleIterator(0.0, 360.0)
        selfMarker = ATAKUtilities.findSelfUnplaced(mapView)
        data.forEach {
            it.update(
                staleTimer = staleTimer,
                selfMarker = selfMarker,
                newOffset = generateBoundedOffset(courseItr, it.getPoint()),
                movementSpeed = movementSpeed,
                newAltitude = updateAltitude(it.getPoint(), it.getPoint().hae)
            )
        }
        return getCotList()
    }

    private fun grabValuesFromPreferences() {
        useRandomCallsigns = prefs.getBooleanFromPair(Prefs.USE_RANDOM_CALLSIGNS)
        useIndexedCallsigns = prefs.getBooleanFromPair(Prefs.USE_INDEXED_CALLSIGN)
        useRandomTeams = prefs.getBooleanFromPair(Prefs.USE_RANDOM_TEAM_COLOURS)
        useRandomRoles = prefs.getBooleanFromPair(Prefs.USE_RANDOM_ROLES)
        iconCount = prefs.parseIntFromPair(Prefs.ICON_COUNT)
        distributionRadius = prefs.parseDoubleFromPair(Prefs.RADIAL_DISTRIBUTION)
        followGps = prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)
        centreLat = prefs.parseDoubleFromPair(Prefs.CENTRE_LATITUDE)
        centreLon = prefs.parseDoubleFromPair(Prefs.CENTRE_LONGITUDE)
        stayAtGroundLevel = prefs.getBooleanFromPair(Prefs.STAY_AT_GROUND_LEVEL)
        centreAlt = if (stayAtGroundLevel) 0.0 else prefs.parseDoubleFromPair(Prefs.CENTRE_ALTITUDE)
        staleTimer = prefs.parseIntFromPair(Prefs.STALE_TIMER)
        movementSpeed = prefs.parseDoubleFromPair(Prefs.MOVEMENT_SPEED)

        /* Stop any fuckery with distribution radii */
        movementSpeed = min(movementSpeed, distributionRadius / 2.0)
        travelDistance = movementSpeed * prefs.parseIntFromPair(Prefs.UPDATE_PERIOD)
    }

    private fun updateDistributionCentre() {
        selfMarker = ATAKUtilities.findSelfUnplaced(mapView)
        distributionCentre = CotPoint(
            centreLatitudeDegrees(),
            centreLongitudeDegrees(),
            centreAltitudeMetres(),
            CotPoint.UNKNOWN,
            CotPoint.UNKNOWN
        )
    }

    private fun generateBoundedOffset(
        courseItr: IRandomStream<Double>,
        startPoint: CotPoint,
        attemptsRemaining: Int = MAX_OFFSET_GENERATION_ATTEMPTS,
    ): Offset {
        Timber.i("generateBoundedOffset $attemptsRemaining")
        if (attemptsRemaining == 0) {
            /* The recursive algorithm has failed too many times, so to avoid a stack overflow we
             * just pick a random point along the radius of the centre-point and generate an Offset
             * that will take us there */
            val offsetToEdge = Offset(travelDistance, courseItr.next())
            val nextPoint = distributionCentre.add(offsetToEdge)
            return Offset.from(startPoint).to(nextPoint)
        } else {
            val offset = Offset(travelDistance, courseItr.next())
            val endPoint = startPoint.add(offset)
            return if (arcdistance(endPoint, distributionCentre) > distributionRadius) {
                /* Invalid offset, so try again */
                generateBoundedOffset(
                    courseItr,
                    startPoint,
                    attemptsRemaining = attemptsRemaining - 1
                )
            } else {
                offset
            }
        }
    }

    private fun doubleIterator(min: Double, max: Double): IRandomStream<Double> {
        return DoubleRandomStream(random, min, max)
    }

    private fun weightedRadialIterator(): IRandomStream<Double> {
        return RadialDistanceRandomStream(random, distributionRadius)
    }

    private fun centreLatitudeDegrees() = if (followGps) {
        selfMarker.point.latitude
    } else {
        centreLat
    }

    private fun centreLongitudeDegrees() = if (followGps) {
        selfMarker.point.longitude
    } else {
        centreLon
    }

    private fun centreAltitudeMetres() = if (stayAtGroundLevel) {
        0.0
    } else {
        centreAlt
    }

    private fun getCotList(): List<CotEvent> {
        return data.map { it.event }
    }

    private fun initialiseAltitude(point: CotPoint, altitudeItr: IRandomStream<Double>): Double {
        val elevationFromDted = getElevationOrZero(point)
        return if (stayAtGroundLevel) {
            if (elevationFromDted == 0.0) {
                /* No DTED to be found at this point, so use the self marker GPS altitude */
                selfMarker.point.altitude
            } else {
                /* We have DTED, so pull elevation from there */
                elevationFromDted
            }
        } else {
            /* Block icons from going underground */
            max(elevationFromDted, altitudeItr.next())
        }
    }

    private fun updateAltitude(point: CotPoint, currentAltitude: Double): Double {
        val elevationFromDted = getElevationOrZero(point)
        return if (stayAtGroundLevel) {
            if (elevationFromDted == 0.0) {
                /* No DTED to be found at this point, so use the self marker GPS altitude */
                selfMarker.point.altitude
            } else {
                /* We have DTED, so pull elevation from there */
                elevationFromDted
            }
        } else {
            /* Direction is either -1, 0 or +1; representing falling, staying steady or
             * rising respectively */
            val direction = IntRandomStream(random, -1, 1).next()
            var newAltitude = currentAltitude + direction * movementSpeed

            /* Not going below ground */
            newAltitude = max(newAltitude, elevationFromDted)

            /* Clip within the bounds of the distribution radius */
            newAltitude = max(newAltitude, centreAlt - distributionRadius)
            newAltitude = min(newAltitude, centreAlt + distributionRadius)

            /* Return our validated altitude */
            newAltitude
        }
    }

    private fun getElevationOrZero(point: CotPoint): Double {
        val elevation = ElevationManager.getElevation(
            point.lat,
            point.lon,
            ELEVATION_QUERY_PARAMETERS
        )
        return if (elevation.isNaN()) 0.0 else elevation
    }

    private companion object {
        const val MAX_OFFSET_GENERATION_ATTEMPTS = 10
        val ELEVATION_QUERY_PARAMETERS = ElevationManager.QueryParameters().also {
            it.elevationModel = ElevationData.MODEL_TERRAIN
        }
    }
}
