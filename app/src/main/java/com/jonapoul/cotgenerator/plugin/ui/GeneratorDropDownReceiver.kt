package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import com.atak.plugins.impl.PluginLayoutInflater
import com.atakmap.android.cot.CotMapComponent
import com.atakmap.android.dropdown.DropDownReceiver
import com.atakmap.android.location.LocationMapComponent
import com.atakmap.android.maps.MapView
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.utils.Intents
import timber.log.Timber


class GeneratorDropDownReceiver(
    mapView: MapView,
    private val pluginContext: Context,
) : DropDownReceiver(mapView) {

    private val rootView = PluginLayoutInflater.inflate(
        pluginContext,
        R.layout.main_layout,
        null
    )

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.i("onReceive ${intent?.action}")
        if (intent?.action == Intents.SHOW_DROP_DOWN_RECEIVER) {
            showDropDown(
                rootView,
                THREE_EIGHTHS_WIDTH, FULL_HEIGHT,
                FULL_WIDTH, THIRD_HEIGHT
            )

            rootView.findViewById<ImageButton>(R.id.about_button).setOnClickListener {
                AboutDialog(mapView.context, pluginContext).show()
            }

            rootView.findViewById<Button>(R.id.start_stop_button).setOnClickListener {
                val externalDispatcher = CotMapComponent.getExternalDispatcher()
                val internalDispatcher = CotMapComponent.getInternalDispatcher()
                val deviceUid = LocationMapComponent._determineBestDeviceUID(mapView.context)
            }
        }
    }

    override fun disposeImpl() {
        Timber.i("disposeImpl")
    }
}
