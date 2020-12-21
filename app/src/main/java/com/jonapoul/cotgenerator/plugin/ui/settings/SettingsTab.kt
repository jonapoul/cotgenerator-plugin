package com.jonapoul.cotgenerator.plugin.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.ui.BaseTab


class SettingsTab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseTab(
    layoutRes = R.layout.tab_settings,
    context,
    attrs,
    defStyleAttr,
    defStyleRes
) {

    override fun onFullyInitialised() {
        TODO("Not yet implemented")
    }
}
