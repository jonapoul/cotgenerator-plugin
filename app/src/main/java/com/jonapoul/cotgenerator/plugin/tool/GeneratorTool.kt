package com.jonapoul.cotgenerator.plugin.tool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.atakmap.android.ipc.AtakBroadcast
import com.jonapoul.cotgenerator.plugin.BuildConfig
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.utils.DebugTree
import com.jonapoul.cotgenerator.plugin.utils.Intents
import timber.log.Timber
import transapps.mapi.MapView
import transapps.maps.plugin.tool.Group
import transapps.maps.plugin.tool.Tool
import transapps.maps.plugin.tool.ToolDescriptor

class GeneratorTool(private val context: Context) : Tool(), ToolDescriptor {

    private val atakBroadcast = AtakBroadcast.getInstance()

    override fun getIcon(): Drawable = ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.plugin_icon,
        context.theme
    )!!

    override fun getShortDescription(): String = context.getString(R.string.app_name)

    override fun getDescription(): String = context.getString(R.string.app_desc)

    override fun getGroups(): Array<Group> = arrayOf(Group.GENERAL)

    override fun onActivate(
        activity: Activity?,
        mapView: MapView?,
        viewGroup: ViewGroup?,
        bundle: Bundle?,
        callback: ToolCallback?,
    ) {
        if (BuildConfig.DEBUG && !hasBeenPlanted) {
            Timber.plant(DebugTree())
            hasBeenPlanted = true
        }
        Timber.i("onActivate")
        // Hack to close the dropdown that automatically opens when a tool plugin is activated.
        callback?.onToolDeactivated(this)

        // Launch the dropdown
        atakBroadcast.sendBroadcast(Intent(Intents.SHOW_DROP_DOWN_RECEIVER))
    }

    override fun onDeactivate(callback: ToolCallback?) {
        Timber.i("onDeactivate")
        if (hasBeenPlanted) {
            Timber.uprootAll()
            hasBeenPlanted = false
        }
    }

    private companion object {
        var hasBeenPlanted = false
    }
}
