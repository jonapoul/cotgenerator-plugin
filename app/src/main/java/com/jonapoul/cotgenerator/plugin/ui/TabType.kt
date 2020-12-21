package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.jonapoul.cotgenerator.plugin.R

internal enum class TabType(
    @StringRes private val labelRes: Int,
    @IdRes val viewId: Int
) {
    HOME(
        labelRes = R.string.tab_home,
        viewId = R.id.tab_home
    ),
    STATUS(
        labelRes = R.string.tab_status,
        viewId = R.id.tab_status
    ),
    SETTINGS(
        labelRes = R.string.tab_settings,
        viewId = R.id.tab_settings
    );

    fun getLabel(context: Context): String = context.getString(labelRes)
}
