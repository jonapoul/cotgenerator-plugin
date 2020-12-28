package com.jonapoul.cotgenerator.plugin.generation

import com.atakmap.coremap.cot.event.CotPoint

internal const val EARTH_RADIUS_METRES = 6.371e6

internal fun arcdistance(p1: CotPoint, p2: CotPoint): Double {
    return p1.toGeoPoint().distanceTo(p2.toGeoPoint())
}

internal object Bearing {
    fun from(startPoint: CotPoint): Builder {
        return Builder(startPoint)
    }

    class Builder(private val startPoint: CotPoint) {
        fun to(endPoint: CotPoint): Degrees {
            return startPoint.toGeoPoint().bearingTo(endPoint.toGeoPoint())
        }
    }
}
