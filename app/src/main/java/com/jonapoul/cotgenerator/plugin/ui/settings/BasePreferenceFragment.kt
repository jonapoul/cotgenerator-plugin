package com.jonapoul.cotgenerator.plugin.ui.settings

import android.content.Context
import androidx.annotation.XmlRes
import com.atakmap.android.preference.PluginPreferenceFragment

abstract class BasePreferenceFragment(
    context: Context,
    @XmlRes resourceId: Int,
) : PluginPreferenceFragment(context, resourceId) {

    init {
        staticContext = context
        staticResourceId = resourceId
    }

    protected companion object {
        var staticContext: Context? = null
        var staticResourceId: Int? = null
    }
}
