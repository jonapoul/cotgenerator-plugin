package com.jonapoul.cotgenerator.plugin.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.MotionEvent
import com.atakmap.android.dropdown.DropDownManager
import com.atakmap.android.dropdown.DropDownReceiver
import com.atakmap.android.ipc.AtakBroadcast
import com.atakmap.android.maps.MapView
import com.atakmap.android.widgets.MapWidget
import com.atakmap.android.widgets.MarkerIconWidget
import com.atakmap.android.widgets.RootLayoutWidget
import com.atakmap.coremap.maps.assets.Icon
import com.jonapoul.cotgenerator.plugin.BuildConfig
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.generation.RunningState
import com.jonapoul.cotgenerator.plugin.utils.Intents


internal class GeneratorStatusWidget(
    mapView: MapView,
    pluginContext: Context,
    private val dropDownReceiver: DropDownReceiver,
) : MarkerIconWidget(),
    MapWidget.OnClickListener,
    RunningState.StateListener {

    private val bottomRightLayout = (mapView.getComponentExtra(ROOT_LAYOUT) as RootLayoutWidget)
        .getLayout(RootLayoutWidget.BOTTOM_RIGHT)

    init {
        name = pluginContext.getString(R.string.widget_name)
        RunningState.addStateListener(this)
        addOnClickListener(this)
        bottomRightLayout.addWidget(this)
        updateIcon(RunningState.getState())
    }

    override fun onMapWidgetClick(widget: MapWidget, event: MotionEvent) {
        if (widget === this) {
            if (dropDownReceiver.isVisible) {
                DropDownManager.getInstance().closeAllDropDowns()
            } else {
                AtakBroadcast.getInstance().sendBroadcast(
                    Intent(Intents.SHOW_DROP_DOWN_RECEIVER)
                )
            }
        }
    }

    override fun onRunningStateChanged(newState: RunningState) {
        updateIcon(newState)
    }

    fun onDestroy() {
        RunningState.removeListener(this)
        removeOnClickListener(this)
        bottomRightLayout.removeWidget(this)
    }

    private fun updateIcon(state: RunningState) {
        val drawableId: Int = state.widgetDrawableId
        val imageUri = "android.resource://${BuildConfig.APPLICATION_ID}/${drawableId}"
        val icon = Icon.Builder().apply {
            setAnchor(0, 0)
            setColor(Icon.STATE_DEFAULT, Color.WHITE)
            setSize(ICON_SIZE, ICON_SIZE)
            setImageUri(Icon.STATE_DEFAULT, imageUri)
        }.build()
        setIcon(icon)
    }

    private companion object {
        const val ICON_SIZE = 32
        const val ROOT_LAYOUT = "rootLayoutWidget"
    }
}
