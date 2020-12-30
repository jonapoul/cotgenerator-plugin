package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.widget.LinearLayout
import com.atakmap.android.maps.MapView

internal abstract class HomeScreenView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    protected val lock = Any()

    protected val mapView: MapView
        get() = mv!!

    protected val pluginContext: Context
        get() = pc!!

    protected val prefs: SharedPreferences
        get() = sp!!

    companion object {
        private var mv: MapView? = null
        private var pc: Context? = null
        private var sp: SharedPreferences? = null

        fun setResources(mapView: MapView, pluginContext: Context) {
            this.mv = mapView
            this.pc = pluginContext
            this.sp = PreferenceManager.getDefaultSharedPreferences(mapView.context)
        }
    }
}
