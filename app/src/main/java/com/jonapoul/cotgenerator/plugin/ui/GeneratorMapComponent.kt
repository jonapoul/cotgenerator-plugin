package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.res.ResourcesCompat
import com.atakmap.android.dropdown.DropDownMapComponent
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter
import com.atakmap.android.maps.MapView
import com.atakmap.app.preferences.ToolsPreferenceFragment
import com.jonapoul.cotgenerator.plugin.utils.Intents
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.ui.settings.MainPreferenceFragment
import timber.log.Timber


class GeneratorMapComponent : DropDownMapComponent() {
    private lateinit var pluginContext: Context

    override fun onCreate(pluginContext: Context, intent: Intent?, mapView: MapView) {
        Timber.i("onCreate")
        pluginContext.setTheme(R.style.ATAKPluginTheme)
        super.onCreate(pluginContext, intent, mapView)
        this.pluginContext = pluginContext

        registerDropDownReceiver(
            GeneratorDropDownReceiver(mapView, pluginContext),
            DocumentedIntentFilter(Intents.SHOW_DROP_DOWN_RECEIVER)
        )
        registerPluginPreferences()
    }

    private fun registerPluginPreferences() {
        ToolsPreferenceFragment.register(
            ToolsPreferenceFragment.ToolPreference(
                pluginContext.getString(R.string.app_name),
                pluginContext.getString(R.string.settings_summary),
                pluginContext.getString(R.string.settings_key),
                ResourcesCompat.getDrawable(
                    pluginContext.resources,
                    R.drawable.icon_48,
                    null
                ),
                MainPreferenceFragment(pluginContext)
            )
        )
    }
}
