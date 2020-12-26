package com.jonapoul.cotgenerator.plugin.ui.home

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageButton
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.ui.BaseTab
import com.jonapoul.cotgenerator.plugin.utils.Toaster


class HomeTab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseTab(
    layoutRes = R.layout.home_tab,
    context,
    attrs,
    defStyleAttr,
    defStyleRes
) {
    private lateinit var startStopButton: Button
    private lateinit var aboutButton: ImageButton

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startStopButton = findViewById(R.id.start_stop_button)
        aboutButton = findViewById(R.id.about_button)

        startStopButton.setOnClickListener {
            Toaster.toast(pluginContext, "TBC")
        }

        aboutButton.setOnClickListener {
            AboutDialog(mapView.context, pluginContext).show()
        }
    }
}
