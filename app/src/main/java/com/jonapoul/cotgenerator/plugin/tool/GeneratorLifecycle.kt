package com.jonapoul.cotgenerator.plugin.tool

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import com.atakmap.android.maps.MapComponent
import com.atakmap.android.maps.MapView
import com.jonapoul.cotgenerator.plugin.BuildConfig
import com.jonapoul.cotgenerator.plugin.ui.GeneratorMapComponent
import com.jonapoul.cotgenerator.plugin.utils.DebugTree
import timber.log.Timber
import transapps.maps.plugin.lifecycle.Lifecycle
import java.util.*

class GeneratorLifecycle(private val pluginContext: Context) : Lifecycle {
    private val mapComponents = ArrayList<MapComponent>()
    private lateinit var mapView: MapView

    override fun onCreate(activity: Activity?, mv: transapps.mapi.MapView?) {
        if (BuildConfig.DEBUG && Timber.forest().isEmpty()) {
            Timber.plant(DebugTree())
        }

        Timber.i("onCreate")
        if (mv == null || mv.view !is MapView) {
            Timber.w("This plugin is only compatible with ATAK MapView")
            return
        }
        mapView = mv.view as MapView

        mapComponents.add(GeneratorMapComponent())

        mapComponents.forEach { it.onCreate(pluginContext, activity?.intent, mapView) }
    }

    override fun onStart() {
        Timber.i("onStart")
        mapComponents.forEach { it.onStart(pluginContext, mapView) }
    }

    override fun onPause() {
        Timber.i("onPause")
        mapComponents.forEach { it.onPause(pluginContext, mapView) }
    }

    override fun onResume() {
        Timber.i("onResume")
        mapComponents.forEach { it.onResume(pluginContext, mapView) }
    }

    override fun onStop() {
        Timber.i("onStop")
        mapComponents.forEach { it.onStop(pluginContext, mapView) }
    }

    override fun onDestroy() {
        Timber.i("onDestroy")
        mapComponents.forEach { it.onDestroy(pluginContext, mapView) }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        Timber.i("onConfigurationChanged")
        mapComponents.forEach { it.onConfigurationChanged(configuration) }
    }

    override fun onFinish() {
        Timber.i("onFinish")
        /* No-op. No corresponding MapComponent method */
    }
}
