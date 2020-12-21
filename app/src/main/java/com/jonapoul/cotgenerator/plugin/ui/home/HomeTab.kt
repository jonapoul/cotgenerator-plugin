package com.jonapoul.cotgenerator.plugin.ui.home

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.Button
import android.widget.Toast
import com.atakmap.android.ipc.AtakBroadcast
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.ui.BaseTab


class HomeTab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseTab(
    layoutRes = R.layout.tab_home,
    context,
    attrs,
    defStyleAttr,
    defStyleRes
) {
    private val atakBroadcast = AtakBroadcast.getInstance()

    private lateinit var startStopButton: Button
    private lateinit var settingsButton: Button


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startStopButton = findViewById(R.id.start_stop_button)
        settingsButton = findViewById(R.id.settings_button)

        startStopButton.setOnClickListener {
            Toast.makeText(pluginContext, "TBC", Toast.LENGTH_SHORT).show()
        }

        settingsButton.setOnClickListener {
            atakBroadcast.sendBroadcast(
                Intent("com.atakmap.app.ADVANCED_SETTINGS")
            )
        }
    }
}
