package com.jonapoul.cotgenerator.plugin.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceManager
import androidx.annotation.StringRes
import com.atakmap.android.gui.PanCheckBoxPreference
import com.atakmap.android.preference.PluginPreferenceFragment
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.ui.Toaster
import com.jonapoul.sharedprefs.PrefPair
import com.jonapoul.sharedprefs.getStringFromPair
import timber.log.Timber


@SuppressLint("ValidFragment")
class GeneratorPreferenceFragment : PluginPreferenceFragment,
    SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener {

    constructor() : super(staticPluginContext, R.xml.preferences)

    @SuppressLint("ValidFragment")
    constructor(
        pluginContext: Context,
        appContext: Context
    ) : super(pluginContext, R.xml.preferences) {
        staticPluginContext = pluginContext
        staticAppContext = appContext
    }

    private lateinit var prefs: SharedPreferences

    /* Small class to hold a preference's key and a string resource to display to the user when
     * they enter an invalid value. */
    private class PrefValidation(val key: String, @StringRes val rationale: Int)

    /* Some preferences which require validation before we accept user input. */
    private val preferenceValidations = listOf(
        PrefValidation(Keys.BASE_CALLSIGN, R.string.rationale_base_callsign),
        PrefValidation(Keys.ICON_COUNT, R.string.rationale_icon_count),
        PrefValidation(Keys.STALE_TIMER, R.string.rationale_stale_timer),
        PrefValidation(Keys.CENTRE_LATITUDE, R.string.rationale_latitude),
        PrefValidation(Keys.CENTRE_LONGITUDE, R.string.rationale_longitude),
        PrefValidation(Keys.CENTRE_ALTITUDE, R.string.rationale_altitude),
        PrefValidation(Keys.RADIAL_DISTRIBUTION, R.string.rationale_radial_distribution),
        PrefValidation(Keys.MOVEMENT_SPEED, R.string.rationale_movement_speed),
        PrefValidation(Keys.UPDATE_PERIOD, R.string.rationale_update_period),
    )

    /* Some preferences which will trigger the disabling/re-enabling of other preferences. */
    private val preferenceAntiDependencies = mapOf(
        Keys.USE_RANDOM_CALLSIGNS to listOf(Keys.BASE_CALLSIGN, Keys.USE_INDEXED_CALLSIGN),
        Keys.USE_RANDOM_TEAM_COLOURS to listOf(Keys.TEAM_COLOUR),
        Keys.USE_RANDOM_ROLES to listOf(Keys.ROLE),
        Keys.FOLLOW_SELF_MARKER to listOf(Keys.CENTRE_LATITUDE, Keys.CENTRE_LONGITUDE),
        Keys.STAY_AT_GROUND_LEVEL to listOf(Keys.CENTRE_ALTITUDE)
    )

    /* Some preferences which should have their values shown as summaries under the preference,
    * along with any suffixes as appropriate. */
    private val preferenceSummaries = mapOf(
        Prefs.BASE_CALLSIGN to null,
        Prefs.TEAM_COLOUR to null,
        Prefs.ROLE to null,
        Prefs.ICON_COUNT to null,
        Prefs.STALE_TIMER to " minutes",
        Prefs.CENTRE_LATITUDE to "°",
        Prefs.CENTRE_LONGITUDE to "°",
        Prefs.CENTRE_ALTITUDE to " metres",
        Prefs.RADIAL_DISTRIBUTION to " metres",
        Prefs.MOVEMENT_SPEED to " m/s",
        Prefs.UPDATE_PERIOD to " seconds"
    )

    override fun getSubTitle(): String = getSubTitle(
        staticPluginContext!!.getString(R.string.preferences_parent),
        staticPluginContext!!.getString(R.string.preferences_title)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        /* Allow the custom preference to access plugin-specific resources when it inflates */
        prefs = PreferenceManager.getDefaultSharedPreferences(staticAppContext)
        RefreshCallsignPreference.setResources(
            pluginContext = pluginContext,
            appContext = staticAppContext!!
        )

        /* Inflate the preferences */
        super.onCreate(savedInstanceState)

        /* Register for update callbacks */
        prefs.registerOnSharedPreferenceChangeListener(this)

        /* Register to validate any user input in required fields */
        preferenceValidations.forEach { findPreference(it.key).onPreferenceChangeListener = this }

        /* Set initial state of some preferences based on their dependencies */
        preferenceAntiDependencies.forEach { setPreferenceDependencyState(it.key, it.value) }

        /* Set summaries and suffixes of preferences */
        preferenceSummaries.forEach { setSummary(it.key, it.value) }

        /* Display the configured base callsign in the explanation for the "use indexed callsigns"
         * option */
        injectBaseCallsignIntoIndexedCallsignSummary()
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any): Boolean {
        /* Match the pref's key to a validation entry. This method will only give callbacks to pref
        * keys that we've explicitly registered, so the exception should never be thrown. */
        val validation = preferenceValidations.find { it.key == preference?.key }
            ?: throw IllegalArgumentException()

        /* Check whether newValue is valid */
        val str = newValue as String
        val result = when (preference?.key) {
            Keys.BASE_CALLSIGN -> validateCallsign(str)
            Keys.ICON_COUNT -> validateInt(str, min = 1, max = 10_000)
            Keys.STALE_TIMER -> validateInt(str, min = 1)
            Keys.CENTRE_LATITUDE -> validateDouble(str, min = -90.0, max = 90.0)
            Keys.CENTRE_LONGITUDE -> validateDouble(str, min = 0.0, max = 360.0)
            Keys.CENTRE_ALTITUDE -> validateDouble(str, min = 0.0)
            Keys.RADIAL_DISTRIBUTION -> validateDouble(str, min = 0.0)
            Keys.MOVEMENT_SPEED -> validateDouble(str, min = 0.0)
            Keys.UPDATE_PERIOD -> validateDouble(str, min = 0.0)
            else -> throw IllegalArgumentException("No validation is done on ${preference?.key}!")
        }

        if (!result) {
            /* It's invalid, so warn the user and reject the input */
            Toaster.toast(
                staticPluginContext!!,
                staticPluginContext!!.getString(validation.rationale)
            )
        }
        return result
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
        Timber.i("onSharedPreferenceChanged $key")
        /* If our changed preference has a method assigned to it, run that method */
        val dependencies = preferenceAntiDependencies[key]
        dependencies?.let { setPreferenceDependencyState(key, it) }

        val pref = preferenceSummaries.keys.firstOrNull { it.key == key }
        if (pref != null) {
            val suffix = preferenceSummaries[pref]
            setSummary(pref, suffix)
        }

        if (key == Keys.BASE_CALLSIGN) {
            injectBaseCallsignIntoIndexedCallsignSummary()
        }
    }

    private fun injectBaseCallsignIntoIndexedCallsignSummary() {
        val indexedCallsignPref = findPreference(Keys.USE_INDEXED_CALLSIGN) as PanCheckBoxPreference
        val baseCallsign = prefs.getStringFromPair(Prefs.BASE_CALLSIGN)
        indexedCallsignPref.summaryOn = pluginContext.getString(
            R.string.summary_on_use_indexed_callsigns,
            baseCallsign,
            baseCallsign
        )
    }

    private fun setPreferenceDependencyState(parentKey: String, dependencyKeys: List<String>) {
        val parentIsEnabled = prefs.getBoolean(parentKey, false)
        dependencyKeys.forEach { findPreference(it).isEnabled = !parentIsEnabled }
    }

    private fun setSummary(pref: PrefPair<String>, suffix: String?) {
        findPreference(pref.key).also {
            val value = prefs.getStringFromPair(pref)
            val appended = if (suffix == null) "" else "$suffix"
            it.summary = "$value$appended"
            Timber.i("setSummary ${pref.key} '$value$appended'")
        }
    }

    private companion object {
        var staticPluginContext: Context? = null
        var staticAppContext: Context? = null
    }
}
