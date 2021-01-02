package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.android.drawing.DrawingPreferences
import com.atakmap.android.drawing.mapItems.DrawingCircle
import com.atakmap.android.maps.MapItem
import com.atakmap.android.maps.MapView
import com.atakmap.coremap.maps.coords.GeoPointMetaData
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.parseDoubleFromPair
import timber.log.Timber


class DrawCircleRunnable(
    private val mapView: MapView,
    private val prefs: SharedPreferences,
    private val mode: Mode,
) : Runnable {

    enum class Mode { DRAW, DELETE }

    private val mapGroup = mapView.rootGroup.findMapGroup(SHAPE_GROUP)

    override fun run() {
        when (mode) {
            Mode.DRAW -> drawCircle()
            Mode.DELETE -> deleteCircle()
        }
    }

    private fun drawCircle() {
        Timber.i("Showing")
        val foundItem: MapItem? = mapGroup.deepFindUID(CIRCLE_UID)
        val circleHasAlreadyBeenDrawn = foundItem is DrawingCircle
        Timber.i("circleHasAlreadyBeenDrawn = $circleHasAlreadyBeenDrawn")

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
                it.radius = prefs.parseDoubleFromPair(Prefs.RADIAL_DISTRIBUTION)
            }
        }

        Timber.i("Setting circle point")
        circle.setCenterPoint(
            GeoPointMetaData.wrap(
                CentrePointFinder.get(mapView, prefs)
            )
        )

        if (!circleHasAlreadyBeenDrawn) {
            /* Draw it on the map */
            Timber.i("Drawing circle")
            mapGroup.addItem(circle)
        }
    }

    private fun deleteCircle() {
        Timber.i("Hiding")
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
