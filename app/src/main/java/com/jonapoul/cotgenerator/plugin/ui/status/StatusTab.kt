package com.jonapoul.cotgenerator.plugin.ui.status

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atakmap.android.maps.MapEvent
import com.atakmap.android.maps.MapEventDispatcher
import com.atakmap.android.util.time.TimeListener
import com.atakmap.android.util.time.TimeViewUpdater
import com.atakmap.coremap.maps.time.CoordinatedTime
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.ui.BaseTab
import com.jonapoul.cotgenerator.plugin.utils.CotMetadata


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

    private lateinit var sortAlphaButton: ImageButton
    private lateinit var sortTimeButton: ImageButton
    private lateinit var sortTeamButton: ImageButton
    private lateinit var sortRoleButton: ImageButton

    private lateinit var timeUpdater: TimeViewUpdater

    override fun onFullyInitialised() {
        adapter = StatusAdapter(pluginContext, mapView)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(
            pluginContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        sortAlphaButton = findViewById(R.id.sort_alpha_button)
        sortTimeButton = findViewById(R.id.sort_time_button)
        sortTeamButton = findViewById(R.id.sort_team_button)
        sortRoleButton = findViewById(R.id.sort_role_button)

        allSortButtons().forEach { entry ->
            entry.value.setOnClickListener {
                setCurrentSortingType(sortingType = entry.key)
            }
        }

        mapView.mapEventDispatcher.also {
            it.addMapEventListener(MapEvent.ITEM_REMOVED, this)
            it.addMapEventListener(MapEvent.ITEM_ADDED, this)
        }

        timeUpdater = TimeViewUpdater(mapView, TIME_UPDATE_FREQUENCY_MS)
        timeUpdater.register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mapView.mapEventDispatcher.also {
            it.removeMapEventListener(MapEvent.ITEM_REMOVED, this)
            it.removeMapEventListener(MapEvent.ITEM_ADDED, this)
        }
        timeUpdater.unregister(this)
    }

    override fun onMapEvent(event: MapEvent) {
        /* Only deal with data with a "role" attribute */
        if (event.item?.hasMetaValue(CotMetadata.ROLE) == false)
            return

        /* Add/remove the new item as required */
        when (event.type) {
            MapEvent.ITEM_ADDED -> adapter.addItem(event.item)
            MapEvent.ITEM_REMOVED -> adapter.removeItem(event.item)
        }

        /* Update the displayed list */
        mapView.post {
            if (isVisible) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onTimeChanged(oldTime: CoordinatedTime?, newTime: CoordinatedTime?) {
        if (isVisible) {
            /* Re-apply our sort once per second. This will also call notifyDataSetChanged and
             * refresh the UI */
            adapter.sortBy(currentSortingType)
        }
    }

    private fun allSortButtons() = mapOf(
        StatusSortingType.ALPHABET to sortAlphaButton,
        StatusSortingType.TIME to sortTimeButton,
        StatusSortingType.TEAM to sortTeamButton,
        StatusSortingType.ROLE to sortRoleButton,
    )

    private fun setCurrentSortingType(sortingType: StatusSortingType) {
        /* Deactivate all buttons, then enable whichever one matches with out sorting type */
        allSortButtons().forEach { it.value.isSelected = false }
        allSortButtons()[sortingType]?.isSelected = true

        /* Tell the adapter to sort our list elements */
        adapter.sortBy(sortingType)

    private companion object {
        const val TIME_UPDATE_FREQUENCY_MS = 1000L
    }
}