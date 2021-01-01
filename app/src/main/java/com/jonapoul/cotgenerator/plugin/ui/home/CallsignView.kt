package com.jonapoul.cotgenerator.plugin.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import com.atakmap.android.gui.EditText
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.prefs.Keys
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.getStringFromPair
import timber.log.Timber


class CallsignView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : HomeScreenView(context, attrs, defStyleAttr, defStyleRes) {

    override val layoutResource = R.layout.home_screen_callsign
    override val titleStringResource = R.string.callsign_section_title

    private val randomiseCheckbox: CheckBox by lazy { findViewById(R.id.callsign_use_random_checkbox) }
    private val indexedCheckbox: CheckBox by lazy { findViewById(R.id.callsign_use_indexed_checkbox) }
    private val baseCallsignEditText: EditText by lazy { findViewById(R.id.callsign_base_edit_text) }
    private val refreshCallsignButton: ImageButton by lazy { findViewById(R.id.callsign_refresh_button) }

    private val atakCallsigns = pluginContext.resources.getStringArray(R.array.callsigns)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshFieldEnabledState()
        setViewsToMatchPreferences()

        randomiseCheckbox.setOnCheckedChangeListener { _, isChecked ->
            refreshFieldEnabledState()
            prefs.edit().putBoolean(Keys.USE_RANDOM_CALLSIGNS, isChecked).apply()
        }

        indexedCheckbox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(Keys.USE_INDEXED_CALLSIGN, isChecked).apply()
        }

        baseCallsignEditText.addTextChangedListener(
            afterTextChanged = {
                prefs.edit().putString(Keys.BASE_CALLSIGN, it.toString()).apply()
            }
        )

        refreshCallsignButton.setOnClickListener {
            val newCallsign = atakCallsigns.random()
            prefs.edit().putString(Keys.BASE_CALLSIGN, newCallsign).apply()
            baseCallsignEditText.setText(newCallsign)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Timber.i("onSharedPreferenceChanged $key")
        when (key) {
            Keys.USE_RANDOM_CALLSIGNS -> refreshFieldEnabledState()
        }
    }

    private fun refreshFieldEnabledState() {
        val useRandomCallsigns = prefs.getBooleanFromPair(Prefs.USE_RANDOM_CALLSIGNS)
        baseCallsignEditText.isEnabled = !useRandomCallsigns
        indexedCheckbox.isEnabled = !useRandomCallsigns
        refreshCallsignButton.isEnabled = !useRandomCallsigns
    }

    private fun setViewsToMatchPreferences() {
        randomiseCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.USE_RANDOM_CALLSIGNS)
        indexedCheckbox.isChecked = prefs.getBooleanFromPair(Prefs.USE_INDEXED_CALLSIGN)
        baseCallsignEditText.setText(prefs.getStringFromPair(Prefs.BASE_CALLSIGN))
    }
}
