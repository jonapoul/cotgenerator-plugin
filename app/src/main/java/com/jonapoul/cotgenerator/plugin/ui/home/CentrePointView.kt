package com.jonapoul.cotgenerator.plugin.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.atakmap.android.toolbar.ToolManagerBroadcastReceiver
import com.atakmap.android.util.ATAKUtilities
import com.atakmap.android.util.time.TimeListener
import com.atakmap.android.util.time.TimeViewUpdater
import com.atakmap.coremap.maps.coords.GeoPoint
import com.atakmap.coremap.maps.time.CoordinatedTime
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.generation.Degrees
import com.jonapoul.cotgenerator.plugin.prefs.Keys
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.cotgenerator.plugin.tool.CentrePointPickerTool
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.parseDoubleFromPair
import timber.log.Timber


class CentrePointView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : HomeScreenView(context, attrs, defStyleAttr, defStyleRes),
    TimeListener {

    override val layoutResource = R.layout.home_screen_centre_point
    override val titleStringResource = R.string.centre_point_section_title

    private val toolManager = ToolManagerBroadcastReceiver.getInstance()

    private val timeUpdater by lazy { TimeViewUpdater(mapView, REFRESH_INTERVAL_MS) }

    private lateinit var selectedPoint: GeoPoint
    private lateinit var selfPoint: GeoPoint

    private val latitudeView: TextView by lazy { findViewById(R.id.text_latitude) }
    private val longitudeView: TextView by lazy { findViewById(R.id.text_longitude) }
    private val altitudeView: TextView by lazy { findViewById(R.id.text_altitude) }
    private val findCentreButton: Button by lazy { findViewById(R.id.find_centre_button) }
    private val followSelfCheckbox: CheckBox by lazy { findViewById(R.id.follow_self_checkbox) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshSelfPoint()
        refreshSelectedPoint()
        refreshUiFields()
        refreshFollowSelfCheckbox()
        setFindCentreButtonState()

        timeUpdater.register(this)

        findViewById<Button>(R.id.pan_to_centre_button).setOnClickListener {
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
        timeUpdater.unregister(this)
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

    override fun onTimeChanged(oldTime: CoordinatedTime?, newTime: CoordinatedTime?) {
        refreshSelfPoint()
        refreshSelectedPoint()
        refreshUiFields()
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
        const val REFRESH_INTERVAL_MS = 1000L

        val CENTRE_POINT_PREFERENCES = listOf(
            Keys.CENTRE_LATITUDE,
            Keys.CENTRE_LONGITUDE,
            Keys.CENTRE_ALTITUDE
        )
    }
}
