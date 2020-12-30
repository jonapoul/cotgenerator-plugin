package com.jonapoul.cotgenerator.plugin.tool

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import com.atakmap.android.gui.CoordDialogView
import com.atakmap.android.maps.MapEvent
import com.atakmap.android.maps.MapEventDispatcher.MapEventDispatchListener
import com.atakmap.android.maps.MapView
import com.atakmap.android.toolbar.Tool
import com.atakmap.android.toolbar.widgets.TextContainer
import com.atakmap.coremap.conversions.CoordinateFormat
import com.atakmap.coremap.maps.coords.GeoPoint
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.prefs.Keys
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.cotgenerator.plugin.ui.Toaster
import com.jonapoul.sharedprefs.getStringFromPair
import timber.log.Timber


class CentrePointPickerTool(
    private val mapView: MapView,
    private val pluginContext: Context
) : Tool(mapView, TOOL_IDENTIFIER),
    MapEventDispatchListener {

    private val appContext = mapView.context

    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    private val textContainer = TextContainer.getTopInstance()

    override fun dispose() {
        Timber.i("dispose")
    }

    override fun onToolBegin(extras: Bundle?): Boolean {
        Timber.i("onToolBegin")
        mapView.mapEventDispatcher.pushListeners()
        clearExtraListeners()
        LISTENED_EVENTS.forEach {
            mapView.mapEventDispatcher.addMapEventListener(it, this)
        }
        textContainer.displayPrompt(
            pluginContext.getString(R.string.centre_point_picker_prompt)
        )
        mapView.mapTouchController.setToolActive(true)
        return super.onToolBegin(extras)
    }

    override fun onMapEvent(event: MapEvent) {
        Timber.i("onMapEvent ${event.type}")
        when (event.type) {
            MapEvent.ITEM_CLICK, MapEvent.MAP_CLICK -> onMapClick(event)
            MapEvent.ITEM_LONG_PRESS, MapEvent.MAP_LONG_PRESS -> onMapLongPress(event)
            else -> requestEndTool()
        }
    }

    override fun onToolEnd() {
        Timber.i("onToolEnd")
        mapView.mapEventDispatcher.clearListeners()
        mapView.mapEventDispatcher.popListeners()
        textContainer.closePrompt()
        mapView.mapTouchController.setToolActive(false)
        super.onToolEnd()
    }

    private fun onMapClick(event: MapEvent) {
        Timber.i("onMapClick ${findPoint(event)?.get()}")
        saveGeoPointIfValid(
            findPoint(event)?.get()
        )
    }

    private fun onMapLongPress(event: MapEvent) {
        Timber.i("onMapLongPress")
        val coordinateFormat = CoordinateFormat.find(
            prefs.getStringFromPair(Prefs.COORDINATE_DISPLAY_FORMAT)
        )

        val coordinateView = LayoutInflater.from(appContext).inflate(
            com.atakmap.app.R.layout.draper_coord_dialog, mapView, false
        ) as CoordDialogView

        val point = findPoint(event)
        if (point != null) {
            coordinateView.setParameters(point, mapView.point, coordinateFormat)
        } else {
            Toaster.toast(pluginContext, R.string.centre_point_picker_error)
            return
        }

        AlertDialog.Builder(appContext)
            .setView(coordinateView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                saveGeoPointIfValid(coordinateView.point?.get())
                dialog.dismiss()
            }.show()
    }

    private fun saveGeoPointIfValid(point: GeoPoint?) {
        Timber.i("savePointIfValid $point")
        if (point == null) {
            Toaster.toast(pluginContext, R.string.centre_point_picker_error)
        } else {
            prefs.edit()
                .putString(Keys.CENTRE_LATITUDE, point.latitude.toString())
                .putString(Keys.CENTRE_LONGITUDE, point.longitude.toString())
                .putString(Keys.CENTRE_ALTITUDE, point.altitude.toString())
                .apply()
        }
        requestEndTool()
    }

    companion object {
        const val TOOL_IDENTIFIER = "centre_point_picker_tool"

        private val LISTENED_EVENTS = listOf(
            MapEvent.ITEM_CLICK,
            MapEvent.MAP_CLICK,
            MapEvent.ITEM_LONG_PRESS,
            MapEvent.MAP_LONG_PRESS
        )
    }
}
