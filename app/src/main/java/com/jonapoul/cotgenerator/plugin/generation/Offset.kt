package com.jonapoul.cotgenerator.plugin.generation

import com.atakmap.coremap.cot.event.CotPoint

internal data class Offset(
    var R: Double,      /* travel distance in metres from previous position */
    var theta: Degrees, /* bearing in degrees */
) {
    companion object {
        fun from(startPoint: CotPoint) = Builder(startPoint)
    }

    class Builder(private val startPoint: CotPoint) {
        fun to(endPoint: CotPoint) = Offset(
            R = arcdistance(startPoint, endPoint),
            theta = Bearing.from(startPoint).to(endPoint)
        )
    }
}