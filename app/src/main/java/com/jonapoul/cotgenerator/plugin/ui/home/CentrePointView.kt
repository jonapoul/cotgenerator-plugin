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
import com.jonapoul.cotgenerator.plugin.generation.CentrePointFinder
import com.jonapoul.cotgenerator.plugin.generation.DrawCircleRunnable
import com.jonapoul.cotgenerator.plugin.generation.RunningState
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

    private val timeUpdater by lazy { TimeViewUpdater(mapView, REFRESH_INTERVAL_MS) }

    private lateinit var selectedPoint: GeoPoint
    private lateinit var selfPoint: GeoPoint

    private val latitudeView: TextView by lazy { findViewById(R.id.text_latitude) }
    private val longitudeView: TextView by lazy { findViewById(R.id.text_longitude) }
    private val altitudeView: TextView by lazy { findViewById(R.id.text_altitude) }
    private val pickNewCentreButton: Button by lazy { findViewById(R.id.pick_new_centre_button) }
    private val followSelfCheckbox: CheckBox by lazy { findViewById(R.id.follow_self_checkbox) }
    private val drawCircleCheckbox: CheckBox by lazy { findViewById(R.id.draw_circle_checkbox) }
    private val groundLevelCheckbox: CheckBox by lazy { findViewById(R.id.ground_level_checkbox) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshSelfPoint()
        refreshSelectedPoint()
        refreshUiFields()
        refreshFollowSelfCheckbox()
        refreshDrawCircleCheckbox()
        refreshGroundLevelCheckbox()
        setFindCentreButtonState()
        runDrawCircleRunnable(
            shouldDraw = prefs.getBooleanFromPair(Prefs.DRAW_CIRCLE)
        )

        timeUpdater.register(this)

        findViewById<Button>(R.id.pan_to_centre_button).setOnClickListener {
            refreshSelectedPoint()
            refreshUiFields()

            /* 0.025 seems to be the magic number to fit the circle on the screen just nicely - at
             * least on my phone. This may be different on other devices, I've no idea. */
            val scale = 0.025 / prefs.parseDoubleFromPair(Prefs.RADIAL_DISTRIBUTION)
            mapView.mapController.panZoomTo(selectedPoint, scale, true)
        }

        pickNewCentreButton.setOnClickListener {
            ToolManagerBroadcastReceiver.getInstance().startTool(
                CentrePointPickerTool.TOOL_IDENTIFIER,
                null
            )
        }

        followSelfCheckbox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(Keys.FOLLOW_SELF_MARKER, isChecked).apply()
        }

        drawCircleCheckbox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(Keys.DRAW_CIRCLE, isChecked).apply()
            runDrawCircleRunnable(shouldDraw = isChecked)
        }

        groundLevelCheckbox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(Keys.STAY_AT_GROUND_LEVEL, isChecked).apply()
        }
    }

    private fun runDrawCircleRunnable(shouldDraw: Boolean) {
        val mode = if (shouldDraw) DrawCircleRunnable.Mode.DRAW else DrawCircleRunnable.Mode.DELETE
        DrawCircleRunnable(mapView, prefs, mode).run()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timeUpdater.unregister(this)

        if (RunningState.getState() == RunningState.STOPPED) {
            /* Delete the circle if the generation isn't running when we close the screen */
            runDrawCircleRunnable(shouldDraw = false)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Timber.i("onSharedPreferenceChanged $key")
        if (CENTRE_POINT_PREFERENCES.contains(key)) {
            refreshSelectedPoint()
            refreshUiFields()
        }
        when (key) {
            Keys.FOLLOW_SELF_MARKER -> {
                refreshFollowSelfCheckbox()
                refreshSelfPoint()
                refreshSelectedPoint()
                refreshUiFields()
                setFindCentreButtonState()
                runDrawCircleRunnable(
                    shouldDraw = prefs.getBooleanFromPair(Prefs.DRAW_CIRCLE)
                )
            }
            Keys.DRAW_CIRCLE -> refreshDrawCircleCheckbox()
            Keys.RADIAL_DISTRIBUTION -> runDrawCircleRunnable(
                shouldDraw = prefs.getBooleanFromPair(Prefs.DRAW_CIRCLE)
            )
        }
    }

    override fun onTimeChanged(oldTime: CoordinatedTime?, newTime: CoordinatedTime?) {
        refreshSelfPoint()
        refreshSelectedPoint()
        refreshUiFields()
        runDrawCircleRunnable(
            shouldDraw = prefs.getBooleanFromPair(Prefs.DRAW_CIRCLE)
        )
    }

    private fun refreshSelectedPoint() {
        selectedPoint = CentrePointFinder.get(mapView, prefs)
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

    private fun refreshDrawCircleCheckbox() {
        drawCircleCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.DRAW_CIRCLE)
    }

    private fun refreshGroundLevelCheckbox() {
        groundLevelCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.STAY_AT_GROUND_LEVEL)
    }

    private fun setFindCentreButtonState() {
        /* Enable the button only if we aren't set to follow this device's GPS */
        pickNewCentreButton.isEnabled = !prefs.getBooleanFromPair(Prefs.FOLLOW_SELF_MARKER)
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
