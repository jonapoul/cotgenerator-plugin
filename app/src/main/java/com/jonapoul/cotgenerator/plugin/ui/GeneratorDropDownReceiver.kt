package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.atak.plugins.impl.PluginLayoutInflater
import com.atakmap.android.dropdown.DropDownReceiver
import com.atakmap.android.maps.MapView
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.generation.RunningState
import com.jonapoul.cotgenerator.plugin.generation.GeneratorThreadManager
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.cotgenerator.plugin.utils.Intents
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.getStringFromPair
import com.jonapoul.sharedprefs.parseIntFromPair
import timber.log.Timber


class GeneratorDropDownReceiver(
    mapView: MapView,
    private val pluginContext: Context,
) : DropDownReceiver(mapView) {

    private val rootView = PluginLayoutInflater.inflate(
        pluginContext,
        R.layout.main_layout,
        null
    )

    private val prefs = PreferenceManager.getDefaultSharedPreferences(pluginContext)

    private val threadManager = GeneratorThreadManager.getInstance()

    private lateinit var startStopButton: Button

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.i("onReceive ${intent?.action}")
        if (intent?.action == Intents.SHOW_DROP_DOWN_RECEIVER) {
            showDropDown(
                rootView,
                THREE_EIGHTHS_WIDTH, FULL_HEIGHT,
                FULL_WIDTH, THIRD_HEIGHT
            )

            rootView.findViewById<ImageButton>(R.id.about_button).setOnClickListener {
                AboutDialog(mapView.context, pluginContext).show()
            }

            startStopButton = rootView.findViewById(R.id.start_stop_button)
            setStartStopButtonState()
            startStopButton.setOnClickListener {
                when (RunningState.getState()) {
                    RunningState.RUNNING -> stop()
                    RunningState.STOPPED -> start()
                }
            }
        }
    }

    override fun disposeImpl() {
        Timber.i("disposeImpl")
    }

    private fun setStartStopButtonState() {
        val runningState = RunningState.getState()
        Timber.i("setStartStopButtonState $runningState")
        startStopButton.setText(runningState.textId)
        val drawable = ContextCompat.getDrawable(pluginContext, runningState.drawableId)
        startStopButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            drawable, null, null, null
        )
    }

    private fun start() {
        Timber.i("start")
        threadManager.start(
            prefs = prefs,
            mapView = mapView,
            callsigns = getCallsigns(),
        )
        RunningState.setState(RunningState.RUNNING)
        setStartStopButtonState()
    }

    private fun stop() {
        Timber.i("stop")
        threadManager.stop()
        RunningState.setState(RunningState.STOPPED)
        setStartStopButtonState()
    }

    private fun getCallsigns(): List<String> {
        val callsigns: MutableList<String> = ArrayList()
        val useRandomCallsigns = prefs.getBooleanFromPair(Prefs.USE_RANDOM_CALLSIGNS)
        val iconCount = prefs.parseIntFromPair(Prefs.ICON_COUNT)
        if (useRandomCallsigns) {
            /* Grab the list of all valid callsigns and shuffle it into a random order */
            val resources = pluginContext.resources
            val allCallsigns = resources.getStringArray(R.array.callsigns)
            allCallsigns.shuffle()
            /* Extract some at random */
            for (i in 0 until iconCount) {
                callsigns.add(allCallsigns[i % allCallsigns.size])
            }
        } else {
            /* Use custom callsign as entered in the settings */
            val baseCallsign = prefs.getStringFromPair(Prefs.BASE_CALLSIGN)
            val useIndexedCallsigns = prefs.getBooleanFromPair(Prefs.USE_INDEXED_CALLSIGN)
            for (i in 0 until iconCount) {
                val callsign = if (useIndexedCallsigns) "${baseCallsign}-${i}" else baseCallsign
                callsigns.add(callsign)
            }
        }
        return callsigns
    }
}
