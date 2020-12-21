package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.atakmap.android.maps.MapView

abstract class BaseFragment : Fragment {
    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    constructor(@LayoutRes layoutRes: Int, context: Context?, map: MapView?) : super(layoutRes) {
        context?.let { pluginContext = it }
        map?.let { mapView = it }
    }

    protected lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(pluginContext)
        return view
    }

    protected companion object {
        lateinit var pluginContext: Context
        lateinit var mapView: MapView
    }
}
