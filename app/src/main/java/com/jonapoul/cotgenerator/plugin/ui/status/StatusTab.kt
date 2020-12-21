package com.jonapoul.cotgenerator.plugin.ui.status

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atakmap.android.gui.PluginSpinner
import com.atakmap.android.maps.MapEvent
import com.atakmap.android.maps.MapEventDispatcher
import com.atakmap.android.util.SimpleItemSelectedListener
import com.atakmap.android.util.time.TimeListener
import com.atakmap.android.util.time.TimeViewUpdater
import com.atakmap.coremap.maps.time.CoordinatedTime
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.ui.BaseTab
import com.jonapoul.cotgenerator.plugin.utils.CotMetadata
import timber.log.Timber


class StatusTab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseTab(
    layoutRes = R.layout.tab_status,
    context,
    attrs,
    defStyleAttr,
    defStyleRes
), MapEventDispatcher.MapEventDispatchListener,
    TimeListener {

    private lateinit var adapter: StatusAdapter

    private lateinit var sortingType: SortingType
    private lateinit var sortingOrder: SortingOrder

    private lateinit var timeUpdater: TimeViewUpdater

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        adapter = StatusAdapter(pluginContext, mapView)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(
            pluginContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        sortingOrder = SortingOrder.getFromPrefs(prefs)
        sortingType = SortingType.getFromPrefs(prefs)

        findViewById<PluginSpinner>(R.id.sort_type_spinner).also {
            it.adapter = ArrayAdapter(pluginContext, SPINNER_LAYOUT, SortingType.array())
            it.onItemSelectedListener = object : SimpleItemSelectedListener() {
                override fun onItemSelected(a: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    Timber.i("Sort Type onItemSelected $position")
                    sortingType = SortingType.atIndex(position)
                    SortingType.saveToPrefs(prefs, sortingType)
                    adapter.sortAndUpdate(sortingType, sortingOrder)
                }
            }
        }

        findViewById<PluginSpinner>(R.id.sort_order_spinner).also {
            it.adapter = ArrayAdapter(pluginContext, SPINNER_LAYOUT, SortingOrder.array())
            it.onItemSelectedListener = object : SimpleItemSelectedListener() {
                override fun onItemSelected(a: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    Timber.i("Sort Order onItemSelected $position")
                    sortingOrder = SortingOrder.atIndex(position)
                    SortingOrder.saveToPrefs(prefs, sortingOrder)
                    adapter.sortAndUpdate(sortingType, sortingOrder)
                }
            }
        }

        mapView.mapEventDispatcher.apply {
            MAP_EVENTS.forEach { addMapEventListener(it, this@StatusTab) }
        }

        timeUpdater = TimeViewUpdater(mapView, TIME_UPDATE_FREQUENCY_MS)
        timeUpdater.register(this)
    }

    override fun onDetachedFromWindow() {
        Timber.i("onDetachedFromWindow")
        super.onDetachedFromWindow()
        mapView.mapEventDispatcher.apply {
            MAP_EVENTS.forEach { removeMapEventListener(it, this@StatusTab) }
        }
        timeUpdater.unregister(this)
    }

    override fun onMapEvent(event: MapEvent) {
        Timber.i("onMapEvent $event")
        /* Only deal with data with a "role" attribute */
        if (event.item?.hasMetaValue(CotMetadata.ROLE) == false)
            return

        /* Add/remove the new item as required */
        when (event.type) {
            MapEvent.ITEM_ADDED -> adapter.addItem(event.item)
            MapEvent.ITEM_REFRESH -> adapter.updateItem(event.item)
            MapEvent.ITEM_REMOVED -> adapter.removeItem(event.item)
        }

        /* Update the displayed list */
        adapter.sortAndUpdate(sortingType, sortingOrder)
    }

    override fun onTimeChanged(oldTime: CoordinatedTime?, newTime: CoordinatedTime?) {
        Timber.d("onTimeChanged")
        if (isVisible) {
            /* Re-apply our sort once per second. This will also call notifyDataSetChanged and
             * refresh the UI */
            adapter.sortAndUpdate(sortingType, sortingOrder)
        }
    }

    private companion object {
        const val TIME_UPDATE_FREQUENCY_MS = 1000L
        const val SPINNER_LAYOUT = android.R.layout.simple_spinner_dropdown_item

        val MAP_EVENTS = listOf(
            MapEvent.ITEM_ADDED,
//            MapEvent.ITEM_REFRESH,
            MapEvent.ITEM_REMOVED
        )
    }
}