package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.atak.plugins.impl.PluginLayoutInflater
import com.atakmap.android.dropdown.DropDownReceiver
import com.atakmap.android.maps.MapView
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.generation.GeneratorThreadManager
import com.jonapoul.cotgenerator.plugin.generation.RunningState
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.cotgenerator.plugin.ui.home.HomeScreenView
import com.jonapoul.cotgenerator.plugin.utils.Intents
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.getStringFromPairNoBlank
import com.jonapoul.sharedprefs.parseIntFromPair
import timber.log.Timber


class GeneratorDropDownReceiver(
    mapView: MapView,
    private val pluginContext: Context,
) : DropDownReceiver(mapView),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val prefs: SharedPreferences

    init {
        HomeScreenView.setResources(mapView, pluginContext)
        prefs = PreferenceManager.getDefaultSharedPreferences(mapView.context)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    private val threadManager = GeneratorThreadManager.getInstance()

    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.i("onReceive ${intent?.action}")
        if (intent?.action == Intents.SHOW_DROP_DOWN_RECEIVER) {
            val rootView = PluginLayoutInflater.inflate(
                pluginContext,
                R.layout.main_layout,
                null
            )

            showDropDown(
                rootView,
                HALF_WIDTH, FULL_HEIGHT,
                FULL_WIDTH, HALF_HEIGHT
            )

            rootView.findViewById<ImageButton>(R.id.about_button).setOnClickListener {
                AboutDialog(mapView.context, pluginContext).show()
            }

            startButton = rootView.findViewById(R.id.start_button)
            stopButton = rootView.findViewById(R.id.stop_button)
            startButton.setOnClickListener { start() }
            stopButton.setOnClickListener { stop() }
        }
    }

    override fun disposeImpl() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        Timber.i("disposeImpl")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (isRunning()) {
            stop()
            start()
        }
    }

    private fun isRunning() = RunningState.getState() == RunningState.RUNNING

    private fun start() {
        Timber.i("start")
        threadManager.start(
            prefs = prefs,
            mapView = mapView,
            callsigns = getCallsigns(),
        )
        RunningState.setState(RunningState.RUNNING)
        toggleButtonVisibility()
    }

    private fun stop() {
        Timber.i("stop")
        threadManager.stop()
        RunningState.setState(RunningState.STOPPED)
        toggleButtonVisibility()
    }

    private fun toggleButtonVisibility() {
        when (RunningState.getState()) {
            RunningState.RUNNING -> {
                startButton.visibility = View.GONE
                stopButton.visibility = View.VISIBLE
            }
            RunningState.STOPPED -> {
                startButton.visibility = View.VISIBLE
                stopButton.visibility = View.GONE
            }
        }
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
            val baseCallsign = prefs.getStringFromPairNoBlank(Prefs.BASE_CALLSIGN)
            val useIndexedCallsigns = prefs.getBooleanFromPair(Prefs.USE_INDEXED_CALLSIGN)
            for (i in 0 until iconCount) {
                val callsign = if (useIndexedCallsigns) "${baseCallsign}-${i}" else baseCallsign
                callsigns.add(callsign)
            }
        }
        return callsigns
    }
}
