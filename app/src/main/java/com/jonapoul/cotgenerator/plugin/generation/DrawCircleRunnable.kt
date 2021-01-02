package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.android.drawing.DrawingPreferences
import com.atakmap.android.drawing.mapItems.DrawingCircle
import com.atakmap.android.maps.MapItem
import com.atakmap.android.maps.MapView
import com.atakmap.coremap.maps.coords.GeoPointMetaData
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.parseDoubleFromPair


class DrawCircleRunnable(
    private val mapView: MapView,
    private val prefs: SharedPreferences,
    private val mode: Mode = Mode.fromPrefs(prefs)
) : Runnable {

    enum class Mode {
        DRAW, DELETE;
        companion object {
            fun fromPrefs(prefs: SharedPreferences): Mode {
                return if (prefs.getBooleanFromPair(Prefs.DRAW_CIRCLE)) DRAW else DELETE
            }
        }
    }

    private val mapGroup = mapView.rootGroup.findMapGroup(SHAPE_GROUP)

    override fun run() {
        when (mode) {
            Mode.DRAW -> drawCircle()
            Mode.DELETE -> deleteCircle()
        }
    }

    private fun drawCircle() {
        val foundItem: MapItem? = mapGroup.deepFindUID(CIRCLE_UID)
        val circleHasAlreadyBeenDrawn = foundItem is DrawingCircle

        val circle = if (circleHasAlreadyBeenDrawn) {
            /* Circle already exists on the map, so just grab it */
            foundItem as DrawingCircle
        } else {
            /* Circle doesn't exist on the map, so create a new one */
            DrawingCircle(mapView, CIRCLE_UID).also {
                val drawingPrefs = DrawingPreferences(mapView)
                it.strokeColor = drawingPrefs.shapeColor
                it.fillColor = drawingPrefs.fillColor
                it.strokeWeight = drawingPrefs.strokeWeight
            }
        }

        circle.radius = prefs.parseDoubleFromPair(Prefs.RADIAL_DISTRIBUTION)
        circle.setCenterPoint(
            GeoPointMetaData.wrap(
                CentrePointFinder.get(mapView, prefs)
            )
        )

        if (!circleHasAlreadyBeenDrawn) {
            /* Draw it on the map */
            mapGroup.addItem(circle)
        }
    }

    private fun deleteCircle() {
        val foundItem: MapItem? = mapGroup.deepFindUID(CIRCLE_UID)
        if (foundItem is DrawingCircle) {
            /* If the item exists and it's a circle, delete it. Otherwise, do nothing */
            foundItem.removeFromGroup()
        }
    }

    private companion object {
        const val CIRCLE_UID = "cot_generator_circle"
        const val SHAPE_GROUP = "Drawing Objects"
    }
}
