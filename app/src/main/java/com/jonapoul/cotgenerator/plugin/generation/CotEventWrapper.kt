package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.android.cot.detail.PrecisionLocationHandler
import com.atakmap.android.cot.detail.TakVersionDetailHandler
import com.atakmap.android.maps.MapItem
import com.atakmap.coremap.cot.event.CotDetail
import com.atakmap.coremap.cot.event.CotEvent
import com.atakmap.coremap.cot.event.CotPoint
import com.atakmap.coremap.maps.time.CoordinatedTime
import com.jonapoul.cotgenerator.plugin.BuildConfig
import com.jonapoul.cotgenerator.plugin.cot.CotRole
import com.jonapoul.cotgenerator.plugin.cot.CotTeam
import com.jonapoul.cotgenerator.plugin.generation.streams.IRandomStream

internal class CotEventWrapper {

    val event = CotEvent()

    private lateinit var offset: Offset

    fun initialise(
        prefs: SharedPreferences,
        uid: String,
        callsign: String,
        selfMarker: MapItem,
        staleTimer: Int,
        movementSpeed: Double,
        precisionLocationHandler: PrecisionLocationHandler
    ): CotEvent {
        event.uid = uid
        event.how = CotEvent.HOW_MACHINE_GENERATED
        event.type = "a-f-G-U-C"
        event.setTimes(CoordinatedTime(), staleTimer)
        event.detail = CotDetail().apply {
            addChild(getContact(callsign))
            addChild(getGroup(prefs))
            addChild(getStatus(selfMarker))
            addChild(TAKV)
            addChild(getTrack(movementSpeed))
            precisionLocationHandler.toCotDetail(selfMarker, event, this)
        }
        return event
    }

    fun update(
        staleTimer: Int,
        movementSpeed: Double,
        selfMarker: MapItem,
        newOffset: Offset,
        newAltitude: Double
    ) {
        event.setTimes(CoordinatedTime(), staleTimer)
        val oldPoint = event.cotPoint
        offset = newOffset
        setPosition(offset, newAltitude)
        val newPoint = event.cotPoint

        event.detail.apply {
            remove("status")
            addChild(getStatus(selfMarker))
            remove("track")
            addChild(getTrack(movementSpeed, course = Bearing.from(oldPoint).to(newPoint)))
        }
    }

    fun setPoint(point: CotPoint, altitude: Double) {
        event.setPoint(
            CotPoint(
                point.lat,
                point.lon,
                altitude,
                CotPoint.UNKNOWN,
                CotPoint.UNKNOWN
            )
        )
    }

    fun getPoint(): CotPoint = event.cotPoint

    fun generateAndUpdateOffset(
        distanceItr: IRandomStream<Double>,
        courseItr: IRandomStream<Double>
    ) {
        offset = generateInitialOffset(distanceItr, courseItr)
        setPosition(offset)
    }

    private fun getContact(callsign: String): CotDetail {
        val contact = CotDetail("contact")
        contact.setAttribute("callsign", callsign)
        return contact
    }

    private fun getGroup(prefs: SharedPreferences): CotDetail {
        val group = CotDetail("__group")
        group.setAttribute("name", CotTeam.fromPrefs(prefs).toString())
        group.setAttribute("role", CotRole.fromPrefs(prefs).toString())
        return group
    }

    private fun getStatus(selfMarker: MapItem): CotDetail {
        val status = CotDetail("status")
        status.setAttribute(
            "battery",
            selfMarker.getMetaLong("battery", 100L).toString()
        )
        return status
    }

    private fun getTrack(speed: Double, course: Double = offset.theta): CotDetail {
        val track = CotDetail("track")
        track.setAttribute("course", course.toString())
        track.setAttribute("speed", speed.toString())
        return track
    }

    private fun generateInitialOffset(
        distanceItr: IRandomStream<Double>,
        courseItr: IRandomStream<Double>
    ): Offset {
        return Offset(
            R = distanceItr.next(),
            theta = courseItr.next()
        )
    }

    private fun setPosition(newOffset: Offset, altitude: Double? = null) {
        val oldPoint = event.cotPoint
        val newPoint = oldPoint.add(newOffset)
        event.setPoint(
            CotPoint(
                newPoint.lat,
                newPoint.lon,
                altitude ?: oldPoint.hae,
                oldPoint.ce,
                oldPoint.le
            )
        )
    }

    private companion object {
        /* Shared instance, since this is the same for all dots and doesn't change over time. */
        val TAKV = CotDetail("takv").also {
            it.setAttribute("platform", "COT-GENERATOR-PLUGIN")
            it.setAttribute("version", BuildConfig.VERSION_NAME)
            it.setAttribute("device", TakVersionDetailHandler.getDeviceDescription())
            it.setAttribute("os", TakVersionDetailHandler.getDeviceOS())
        }
    }
}