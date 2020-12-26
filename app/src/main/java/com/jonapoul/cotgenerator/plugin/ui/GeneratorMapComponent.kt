package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.res.ResourcesCompat
import com.atakmap.android.dropdown.DropDownMapComponent
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter
import com.atakmap.android.maps.MapView
import com.atakmap.app.preferences.ToolsPreferenceFragment
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.ui.settings.GeneratorPreferenceFragment
import com.jonapoul.cotgenerator.plugin.utils.Intents
import timber.log.Timber


class GeneratorMapComponent : DropDownMapComponent() {

    override fun onCreate(pluginContext: Context, intent: Intent?, mapView: MapView) {
        Timber.i("onCreate")
        pluginContext.setTheme(R.style.ATAKPluginTheme)
        super.onCreate(pluginContext, intent, mapView)

        registerDropDownReceiver(
            GeneratorDropDownReceiver(mapView, pluginContext),
            DocumentedIntentFilter(Intents.SHOW_DROP_DOWN_RECEIVER)
        )

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
}
