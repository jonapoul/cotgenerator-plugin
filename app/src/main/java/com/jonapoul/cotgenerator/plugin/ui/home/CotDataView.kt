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
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.getStringFromPair
import com.jonapoul.sharedprefs.parseIntFromPair
import java.lang.NumberFormatException


class CotDataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : HomeScreenView(context, attrs, defStyleAttr, defStyleRes) {

    override val layoutResource = R.layout.home_screen_cot_data
    override val titleStringResource = R.string.cot_data_section_title

    private val iconCountSeekbar: SeekBar by lazy { findViewById(R.id.cot_data_icon_count_seek_bar) }
    private val iconCountTextView: TextView by lazy { findViewById(R.id.cot_data_icon_count_text_view) }

    private val radiusSeekbar: SeekBar by lazy { findViewById(R.id.cot_data_radius_seek_bar) }
    private val radiusTextView: TextView by lazy { findViewById(R.id.cot_data_radius_text_view) }

    private val randomTeamCheckbox: CheckBox by lazy { findViewById(R.id.cot_data_team_use_random_checkbox) }
    private val randomRoleCheckbox: CheckBox by lazy { findViewById(R.id.cot_data_role_use_random_checkbox) }

    private val teamSpinner: PluginSpinner by lazy { findViewById(R.id.cot_data_team_spinner) }
    private val roleSpinner: PluginSpinner by lazy { findViewById(R.id.cot_data_role_spinner) }

    private val teamTextView: TextView by lazy { findViewById(R.id.cot_data_team_text_view) }
    private val roleTextView: TextView by lazy { findViewById(R.id.cot_data_role_text_view) }

    private val allTeams by lazy { pluginContext.resources.getStringArray(R.array.teams) }
    private val allRoles by lazy { pluginContext.resources.getStringArray(R.array.roles) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setValuesFromPreferences()
        refreshTeamEnabledState()
        refreshRoleEnabledState()

        setCheckBoxListener(randomTeamCheckbox, Keys.USE_RANDOM_TEAM_COLOURS)
        setCheckBoxListener(randomRoleCheckbox, Keys.USE_RANDOM_ROLES)

        setSpinnerAdapter(teamSpinner, R.array.teams)
        setSpinnerAdapter(roleSpinner, R.array.roles)

        setSpinnerListener(teamSpinner, allTeams, Keys.TEAM_COLOUR)
        setSpinnerListener(roleSpinner, allRoles, Keys.ROLE)

        setIconCountTextValue()
        setIconCountSeekbarValue()
        setIconCountSeekbarListener()

        setRadiusTextValue()
        setRadiusSeekbarValue()
        setRadiusSeekbarListener()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            Keys.USE_RANDOM_TEAM_COLOURS -> refreshTeamEnabledState()
            Keys.USE_RANDOM_ROLES -> refreshRoleEnabledState()
            Keys.ICON_COUNT -> setIconCountTextValue()
            Keys.RADIAL_DISTRIBUTION -> setRadiusTextValue()
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
        randomTeamCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.USE_RANDOM_TEAM_COLOURS)
        randomRoleCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.USE_RANDOM_ROLES)

        teamSpinner.setSelection(
            allTeams.indexOf(prefs.getStringFromPair(Prefs.TEAM_COLOUR))
        )
        roleSpinner.setSelection(
            allRoles.indexOf(prefs.getStringFromPair(Prefs.ROLE))
        )
    }

    private fun refreshTeamEnabledState() {
        val useRandomTeams = prefs.getBooleanFromPair(Prefs.USE_RANDOM_TEAM_COLOURS)
        teamSpinner.isEnabled = !useRandomTeams
        setTextViewEnabled(teamTextView, !useRandomTeams)
    }

    private fun refreshRoleEnabledState() {
        val useRandomRoles = prefs.getBooleanFromPair(Prefs.USE_RANDOM_ROLES)
        roleSpinner.isEnabled = !useRandomRoles
        setTextViewEnabled(roleTextView, !useRandomRoles)
    }

    private fun setIconCountTextValue() {
        val count = prefs.parseIntFromPair(Prefs.ICON_COUNT)
        iconCountTextView.text = when {
            count < 1000 -> count.toString()
            else -> "${count / 1000}k"
        }
    }

    private fun setIconCountSeekbarValue() {
        try {
            val iconCount = prefs.parseIntFromPair(Prefs.ICON_COUNT)
            iconCountSeekbar.progress = ICON_COUNT_TICKS.indexOf(iconCount)
        } catch (e: NumberFormatException) {
            iconCountSeekbar.progress = 1
        }
    }

    private fun setIconCountSeekbarListener() {
        iconCountSeekbar.setOnSeekBarChangeListener(
            object : SimpleOnSeekBarChangeListener() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val count = ICON_COUNT_TICKS[progress]
                    prefs.edit().putString(Keys.ICON_COUNT, count.toString()).apply()
                }
            }
        )
    }

    private fun setRadiusTextValue() {
        val radius = prefs.parseIntFromPair(Prefs.RADIAL_DISTRIBUTION)
        radiusTextView.text = when {
            radius < 1000 -> "$radius m"
            else -> "${radius / 1000} km"
        }
    }

    private fun setRadiusSeekbarValue() {
        radiusSeekbar.progress = try {
            val radius = prefs.parseIntFromPair(Prefs.RADIAL_DISTRIBUTION)
            RADIUS_TICKS.indexOf(radius)
        } catch (e: NumberFormatException) {
            1
        }
    }

    private fun setRadiusSeekbarListener() {
        radiusSeekbar.setOnSeekBarChangeListener(
            object : SimpleOnSeekBarChangeListener() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val radius = RADIUS_TICKS[progress]
                    prefs.edit().putString(Keys.RADIAL_DISTRIBUTION, radius.toString()).apply()
                }
            }
        )
    }

    private abstract class SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) { /* No-op */ }

        override fun onStopTrackingTouch(seekBar: SeekBar?) { /* No-op */ }
    }

    private companion object {
        val ICON_COUNT_TICKS = listOf(1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000)
        val RADIUS_TICKS = listOf(1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000)
    }
}
