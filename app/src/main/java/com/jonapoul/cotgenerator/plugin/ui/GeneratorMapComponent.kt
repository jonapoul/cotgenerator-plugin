package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.res.ResourcesCompat
import com.atakmap.android.dropdown.DropDownMapComponent
import com.atakmap.android.dropdown.DropDownReceiver
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter
import com.atakmap.android.maps.MapView
import com.atakmap.android.toolbar.ToolManagerBroadcastReceiver
import com.atakmap.app.preferences.ToolsPreferenceFragment
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.prefs.GeneratorPreferenceFragment
import com.jonapoul.cotgenerator.plugin.tool.CentrePointPickerTool
import com.jonapoul.cotgenerator.plugin.utils.Intents
import timber.log.Timber


class GeneratorMapComponent : DropDownMapComponent() {

    private val toolManager = ToolManagerBroadcastReceiver.getInstance()

    private lateinit var statusWidget: GeneratorStatusWidget
    private lateinit var dropdownReceiver: DropDownReceiver

    override fun onCreate(pluginContext: Context, intent: Intent?, mapView: MapView) {
        Timber.i("onCreate")
        pluginContext.setTheme(R.style.ATAKPluginTheme)
        super.onCreate(pluginContext, intent, mapView)

        /* Register our UI */
        dropdownReceiver = GeneratorDropDownReceiver(mapView, pluginContext)
        registerDropDownReceiver(
            dropdownReceiver,
            DocumentedIntentFilter(Intents.SHOW_DROP_DOWN_RECEIVER)
        )

        /* Register the custom point picker tool */
        toolManager.registerTool(
            CentrePointPickerTool.TOOL_IDENTIFIER,
            CentrePointPickerTool(mapView, pluginContext)
        )

        /* Set up a widget to show activity status, and give a quick link to open the window */
        statusWidget = GeneratorStatusWidget(mapView, pluginContext, dropdownReceiver)

        /* Register the preference fragment */
        ToolsPreferenceFragment.register(
            ToolsPreferenceFragment.ToolPreference(
                pluginContext.getString(R.string.preferences_title),
                pluginContext.getString(R.string.preferences_summary),
                pluginContext.getString(R.string.preferences_key),
                ResourcesCompat.getDrawable(
                    pluginContext.resources,
                    R.drawable.plugin_icon,
                    null
                ),
                GeneratorPreferenceFragment(
                    pluginContext,
                    appContext = mapView.context
                )
            )
        )
    }

    override fun onDestroyImpl(context: Context?, view: MapView?) {
        Timber.i("onDestroyImpl")
        super.onDestroyImpl(context, view)
        toolManager.unregisterTool(CentrePointPickerTool.TOOL_IDENTIFIER)
        statusWidget.onDestroy()
    }
}
