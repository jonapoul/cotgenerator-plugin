package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.Intent
import android.widget.TabHost
import com.atak.plugins.impl.PluginLayoutInflater
import com.atakmap.android.dropdown.DropDownReceiver
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

    init {
        val tabHost: TabHost = rootView.findViewById(R.id.tab_host)
        tabHost.setup()
        TabType.values().forEach { addTab(tabHost, it) }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.i("onReceive ${intent?.action}")
        if (intent?.action == Intents.SHOW_DROP_DOWN_RECEIVER) {
            showDropDown(
                rootView,
                THREE_EIGHTHS_WIDTH, FULL_HEIGHT,
                FULL_WIDTH, THIRD_HEIGHT
            )
        }
    }

    override fun disposeImpl() {
        Timber.i("disposeImpl")
    }

    private fun addTab(tabHost: TabHost, tabType: TabType) {
        Timber.i("addTab $tabType")
        val tabSpec = tabHost.newTabSpec(tabType.getLabel(pluginContext))
        tabSpec.setContent(tabType.viewId)
        tabSpec.setIndicator(tabType.getLabel(pluginContext))
        tabHost.addTab(tabSpec)

        val tab = rootView.findViewById<BaseTab>(tabType.viewId)
        tab.setMap(mapView)
    }
}
