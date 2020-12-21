package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.AttributeSet
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
        Timber.i("init ${this.javaClass.simpleName}")
    }

    protected lateinit var prefs: SharedPreferences
    protected lateinit var mapView: MapView

    fun setMap(mv: MapView) {
        Timber.i("setMap $mv")
        mapView = mv
        prefs = PreferenceManager.getDefaultSharedPreferences(mapView.context)
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Timber.i("onAttachedToWindow ${this.javaClass.simpleName}")
    }
}
