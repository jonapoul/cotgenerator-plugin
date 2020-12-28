package com.jonapoul.cotgenerator.plugin.generation

import com.atakmap.coremap.cot.event.CotDetail
import com.atakmap.coremap.cot.event.CotEvent
import com.atakmap.coremap.cot.event.CotPoint
import com.atakmap.coremap.maps.time.CoordinatedTime
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal fun CotPoint.add(offset: Offset): CotPoint {
    val rOverR = offset.R / EARTH_RADIUS_METRES
    val theta = offset.theta.toRadians() // travel bearing in radians

    /* Start position, both in radians */
    val lat1 = lat.toRadians()
    val lon1 = lon.toRadians()

    /* End position, both in radians */
    val lat2 = asin(sin(lat1) * cos(rOverR) + cos(lat1) * sin(rOverR) * cos(theta))
    val lon2 = lon1 + atan2(
        sin(theta) * sin(rOverR) * cos(lat1),
        cos(rOverR) - sin(lat1) * sin(lat2)
    )
    return CotPoint(
        lat2.toDegrees(),
        lon2.toDegrees(),
        this.hae,
        this.ce,
        this.le
    )
}

internal fun CotEvent.setTimes(now: CoordinatedTime, staleTimerMins: Int) {
    time = now
    start = now
    stale = now.addMinutes(staleTimerMins)
}

internal fun CotDetail.remove(tag: String) {
    findChild(tag)?.let {
        removeChild(it)
    }
}

internal fun CotDetail.findChild(tag: String): CotDetail? {
    val children = this.getChildrenByName(tag)
    return if (children.size > 0) children[0] else null
}
