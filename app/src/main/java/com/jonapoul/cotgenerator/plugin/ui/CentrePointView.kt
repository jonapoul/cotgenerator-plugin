package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.atakmap.android.toolbar.ToolManagerBroadcastReceiver
import com.atakmap.android.util.ATAKUtilities
import com.atakmap.coremap.maps.coords.GeoPoint
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.generation.Degrees
import com.jonapoul.cotgenerator.plugin.prefs.Keys
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.cotgenerator.plugin.tool.CentrePointPickerTool
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.parseDoubleFromPair
import timber.log.Timber


internal class CentrePointView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : HomeScreenView(context, attrs, defStyleAttr, defStyleRes),
    SharedPreferences.OnSharedPreferenceChangeListener {

    init {
        inflate(pluginContext, R.layout.main_section_centre_point, this)
    }

    private val toolManager = ToolManagerBroadcastReceiver.getInstance()

    private lateinit var selectedPoint: GeoPoint
    private lateinit var selfPoint: GeoPoint

    private val latitudeView: TextView by lazy { findViewById(R.id.text_latitude) }
    private val longitudeView: TextView by lazy { findViewById(R.id.text_longitude) }
    private val altitudeView: TextView by lazy { findViewById(R.id.text_altitude) }
    private val findCentreButton: ImageButton by lazy { findViewById(R.id.find_centre_button) }
    private val followSelfCheckbox: CheckBox by lazy { findViewById(R.id.follow_self_checkbox) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshSelfPoint()
        refreshSelectedPoint()
        refreshUiFields()
        refreshFollowSelfCheckbox()
        setFindCentreButtonState()

        prefs.registerOnSharedPreferenceChangeListener(this)

        findViewById<ImageButton>(R.id.pan_to_centre_button).setOnClickListener {
            refreshSelectedPoint()
            refreshUiFields()
            mapView.mapController.panTo(selectedPoint, true)
        }

        findCentreButton.setOnClickListener {
            toolManager.startTool(
                CentrePointPickerTool.TOOL_IDENTIFIER,
                null
            )
        }

        followSelfCheckbox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(Keys.FOLLOW_SELF_MARKER, isChecked).apply()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Timber.i("onSharedPreferenceChanged $key")
        if (CENTRE_POINT_PREFERENCES.contains(key)) {
            refreshSelectedPoint()
            refreshUiFields()
        } else if (key == Keys.FOLLOW_SELF_MARKER) {
            refreshFollowSelfCheckbox()
            refreshSelfPoint()
            refreshSelectedPoint()
            refreshUiFields()
            setFindCentreButtonState()
        }
    }

    private fun getLatitude(): Degrees {
        return if (prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)) {
            selfPoint.latitude
        } else {
            prefs.parseDoubleFromPair(Prefs.CENTRE_LATITUDE)
        }
    }

    private fun getLongitude(): Degrees {
        return if (prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)) {
            selfPoint.longitude
        } else {
            prefs.parseDoubleFromPair(Prefs.CENTRE_LONGITUDE)
        }
    }

    private fun getAltitude(): Double {
        return if (prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)) {
            selfPoint.altitude
        } else {
            prefs.parseDoubleFromPair(Prefs.CENTRE_ALTITUDE)
        }
    }

    private fun refreshSelectedPoint() {
        selectedPoint = GeoPoint(getLatitude(), getLongitude(), getAltitude())
    }

    private fun refreshSelfPoint() {
        selfPoint = ATAKUtilities.findSelfUnplaced(mapView).point
    }

    private fun refreshUiFields() {
        latitudeView.text = pluginContext.getString(
            R.string.centre_point_degrees_placeholder,
            selectedPoint.latitude
        )
        longitudeView.text = pluginContext.getString(
            R.string.centre_point_degrees_placeholder,
            selectedPoint.longitude
        )

        val altitude = if (selectedPoint.altitude.isNaN()) 0.0 else selectedPoint.altitude
        altitudeView.text = pluginContext.getString(
            R.string.centre_point_hae_metres_placeholder,
            altitude
        )
    }

    private fun refreshFollowSelfCheckbox() {
        followSelfCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)
    }

    private fun setFindCentreButtonState() {
        /* Enable the button only if we aren't set to follow this device's GPS */
        findCentreButton.isEnabled = !prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)
    }

    private companion object {
        val CENTRE_POINT_PREFERENCES = listOf(
            Keys.CENTRE_LATITUDE,
            Keys.CENTRE_LONGITUDE,
            Keys.CENTRE_ALTITUDE
        )
    }
}
