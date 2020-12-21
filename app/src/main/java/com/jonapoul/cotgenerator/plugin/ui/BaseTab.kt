package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import com.atakmap.android.maps.MapView
import timber.log.Timber

abstract class BaseTab @JvmOverloads constructor(
    @LayoutRes layoutRes: Int,
    protected val pluginContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(pluginContext, attrs, defStyleAttr, defStyleRes) {

    init {
        inflate(pluginContext, layoutRes, this)
        Timber.d("init ${this.javaClass.simpleName}")
    }

    private var hasInflated = false
    private var hasMapView = false
    private var hasInitialised = false

    protected val prefs = PreferenceManager.getDefaultSharedPreferences(pluginContext)
    protected lateinit var mapView: MapView

    protected abstract fun onFullyInitialised()

    fun setMap(mv: MapView) {
        Timber.d("setMap $mv")
        mapView = mv
        hasMapView = true
        if (shouldInitialise()) {
            onFullyInitialised()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Timber.d("onFinishInflate ${this.javaClass.simpleName}")
        hasInflated = true
        if (shouldInitialise()) {
            onFullyInitialised()
        }
    }

    private fun shouldInitialise() = hasMapView and hasInflated and !hasInitialised
}
