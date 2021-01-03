package com.jonapoul.cotgenerator.plugin.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.annotation.ArrayRes
import com.atakmap.android.gui.PluginSpinner
import com.atakmap.android.util.SimpleItemSelectedListener
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.prefs.Keys
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.*

class CotDataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : HomeScreenView(context, attrs, defStyleAttr, defStyleRes) {

    override val layoutResource = R.layout.home_screen_cot_data
    override val titleStringResource = R.string.cot_data_section_title
    override val isVisiblePref = Prefs.IS_VISIBLE_COT_DATA

    private val iconCountSeekbar: SeekBar by lazy { findViewById(R.id.cot_data_icon_count_seek_bar) }
    private val iconCountTextView: TextView by lazy { findViewById(R.id.cot_data_icon_count_text_view) }

    private val radiusSeekbar: SeekBar by lazy { findViewById(R.id.cot_data_radius_seek_bar) }
    private val radiusTextView: TextView by lazy { findViewById(R.id.cot_data_radius_text_view) }

    private val staleSeekbar: SeekBar by lazy { findViewById(R.id.cot_data_stale_seek_bar) }
    private val staleTextView: TextView by lazy { findViewById(R.id.cot_data_stale_text_view) }

    private val speedSeekbar: SeekBar by lazy { findViewById(R.id.cot_data_speed_seek_bar) }
    private val speedTextView: TextView by lazy { findViewById(R.id.cot_data_speed_text_view) }

    private val periodSeekbar: SeekBar by lazy { findViewById(R.id.cot_data_period_seek_bar) }
    private val periodTextView: TextView by lazy { findViewById(R.id.cot_data_period_text_view) }

    private val randomTeamCheckbox: CheckBox by lazy { findViewById(R.id.cot_data_team_use_random_checkbox) }
    private val teamSpinner: PluginSpinner by lazy { findViewById(R.id.cot_data_team_spinner) }
    private val teamTextView: TextView by lazy { findViewById(R.id.cot_data_team_text_view) }
    private val allTeams by lazy { pluginContext.resources.getStringArray(R.array.teams) }

    private val randomRoleCheckbox: CheckBox by lazy { findViewById(R.id.cot_data_role_use_random_checkbox) }
    private val roleSpinner: PluginSpinner by lazy { findViewById(R.id.cot_data_role_spinner) }
    private val roleTextView: TextView by lazy { findViewById(R.id.cot_data_role_text_view) }
    private val allRoles by lazy { pluginContext.resources.getStringArray(R.array.roles) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshTeamEnabledState()
        refreshRoleEnabledState()

        setCheckBoxListener(randomTeamCheckbox, Keys.USE_RANDOM_TEAMS)
        setCheckBoxListener(randomRoleCheckbox, Keys.USE_RANDOM_ROLES)

        setSpinnerAdapter(teamSpinner, R.array.teams)
        setSpinnerAdapter(roleSpinner, R.array.roles)

        setSpinnerListener(teamSpinner, allTeams, Keys.TEAM)
        setSpinnerListener(roleSpinner, allRoles, Keys.ROLE)

        setValuesFromPreferences()

        initialiseSeekBar(iconCountSeekbar, iconCountTextView, Prefs.COUNT, COUNT_TICKS, "", "k")
        initialiseSeekBar(radiusSeekbar, radiusTextView, Prefs.RADIUS, RADIUS_TICKS, " m", " km")
        initialiseSeekBar(staleSeekbar, staleTextView, Prefs.STALE_TIMER, STALE_TICKS, " min", " min")
        initialiseSeekBar(speedSeekbar, speedTextView, Prefs.SPEED, SPEED_TICKS, " m/s", " km/s")
        initialiseSeekBar(periodSeekbar, periodTextView, Prefs.PERIOD, PERIOD_TICKS, "s", "s")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            Keys.USE_RANDOM_TEAMS -> refreshTeamEnabledState()
            Keys.USE_RANDOM_ROLES -> refreshRoleEnabledState()
            Keys.COUNT -> setSeekBarLabel(iconCountTextView, Prefs.COUNT, "", "k")
            Keys.RADIUS -> setSeekBarLabel(radiusTextView, Prefs.RADIUS, " m", " km")
            Keys.STALE_TIMER -> setSeekBarLabel(staleTextView, Prefs.STALE_TIMER, " min", " min")
            Keys.SPEED -> setSeekBarLabel(speedTextView, Prefs.SPEED, " m/s", " km/s")
            Keys.PERIOD -> setSeekBarLabel(periodTextView, Prefs.PERIOD, "s", "s")
        }
    }

    private fun setCheckBoxListener(checkBox: CheckBox, key: String) {
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(key, isChecked).apply()
        }
    }

    private fun setSpinnerAdapter(spinner: PluginSpinner, @ArrayRes arrayRes: Int) {
        spinner.adapter = ArrayAdapter.createFromResource(
            pluginContext,
            arrayRes,
            R.layout.spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun setSpinnerListener(spinner: PluginSpinner, array: Array<String>, key: String) {
        spinner.onItemSelectedListener = object : SimpleItemSelectedListener() {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                prefs.edit().putString(key, array[position]).apply()
            }
        }
    }

    private fun setValuesFromPreferences() {
        randomTeamCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.USE_RANDOM_TEAMS)
        randomRoleCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.USE_RANDOM_ROLES)

        teamSpinner.setSelection(
            allTeams.indexOf(prefs.getStringFromPair(Prefs.TEAM))
        )
        roleSpinner.setSelection(
            allRoles.indexOf(prefs.getStringFromPair(Prefs.ROLE))
        )
    }

    private fun refreshTeamEnabledState() {
        val useRandomTeams = prefs.getBooleanFromPair(Prefs.USE_RANDOM_TEAMS)
        teamSpinner.isEnabled = !useRandomTeams
        setTextViewEnabled(teamTextView, !useRandomTeams)
    }

    private fun refreshRoleEnabledState() {
        val useRandomRoles = prefs.getBooleanFromPair(Prefs.USE_RANDOM_ROLES)
        roleSpinner.isEnabled = !useRandomRoles
        setTextViewEnabled(roleTextView, !useRandomRoles)
    }

    private fun initialiseSeekBar(
        seekBar: SeekBar,
        textView: TextView,
        pref: PrefPair<Int>,
        ticks: List<Int>,
        regularSuffix: String,
        thousandsSuffix: String,
    ) {
        setSeekBarLabel(textView, pref, regularSuffix, thousandsSuffix)
        setSeekBarValue(seekBar, pref, ticks)
        setSeekBarListener(seekBar, pref, ticks)
    }

    private fun setSeekBarLabel(
        textView: TextView,
        pref: PrefPair<Int>,
        regularSuffix: String,
        thousandsSuffix: String,
    ) {
        val value = prefs.getIntFromPair(pref)
        textView.text = when {
            value < 1000 -> "$value$regularSuffix"
            else -> "${value / 1000}$thousandsSuffix"
        }
    }

    private fun setSeekBarValue(seekBar: SeekBar, pref: PrefPair<Int>, ticks: List<Int>) {
        seekBar.progress = try {
            ticks.indexOf(prefs.getIntFromPair(pref))
        } catch (e: NumberFormatException) {
            1
        }
    }

    private fun setSeekBarListener(seekBar: SeekBar, pref: PrefPair<Int>, ticks: List<Int>) {
        seekBar.setOnSeekBarChangeListener(
            object : SimpleOnSeekBarChangeListener() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    prefs.edit().putInt(pref.key, ticks[progress]).apply()
                }
            }
        )
    }

    private abstract class SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) { /* No-op */ }
        override fun onStopTrackingTouch(seekBar: SeekBar?) { /* No-op */ }
    }

    private companion object {
        val COUNT_TICKS = listOf(1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000)
        val RADIUS_TICKS = listOf(1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000)
        val STALE_TICKS = List(60) { it + 1 } // list from 1 to 60 minutes inclusive
        val SPEED_TICKS = listOf(1, 2, 5, 10, 20, 50, 100, 200, 500, 1000)
        val PERIOD_TICKS = List(60) { it + 1 } // list from 1 to 60 seconds inclusive
    }
}
