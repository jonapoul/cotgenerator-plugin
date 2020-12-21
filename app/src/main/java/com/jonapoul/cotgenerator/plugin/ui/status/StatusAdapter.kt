package com.jonapoul.cotgenerator.plugin.ui.status

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atakmap.android.maps.MapGroup
import com.atakmap.android.maps.MapItem
import com.atakmap.android.maps.MapTouchController
import com.atakmap.android.maps.MapView
import com.atakmap.android.util.ATAKUtilities
import com.atakmap.coremap.maps.time.CoordinatedTime
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.utils.CotMetadata
import com.jonapoul.cotgenerator.plugin.utils.TimeUtils


internal class StatusAdapter(
    private val pluginContext: Context,
    mapView: MapView,
) : RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    private val mapItems = ArrayList<MapItem>()

    init {
        addItems(mapView.rootGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(pluginContext)
                .inflate(R.layout.status_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mapItem = mapItems[position]

        holder.index.text = pluginContext.getString(
            R.string.status_row_index,
            position,
            itemCount
        )
        ATAKUtilities.setIcon(holder.icon, mapItem)
        holder.callsign.text = mapItem.title

        val now = CoordinatedTime().milliseconds
        val elapsed = now - mapItem.getMetaLong("lastUpdateTime", 0)
        holder.lastUpdate.text = TimeUtils.lastHeardTime(elapsed)
    }

    override fun getItemCount(): Int = mapItems.size

    fun addItem(item: MapItem) {
        mapItems.add(item)
        notifyDataSetChanged()
    }

    fun updateItem(item: MapItem) {
        val index = mapItems.indexOfFirst { it.uid == item.uid }
        if (index == -1) {
            addItem(item)
        } else {
            mapItems[index] = item
        }
    }

    fun removeItem(item: MapItem) {
        mapItems.remove(item)
        notifyDataSetChanged()
    }

    fun sortAndUpdate(sortingType: SortingType, sortingOrder: SortingOrder) {
        /* I couldn't figure out a nicer-looking way of doing this. Deal with it */
        when (sortingOrder) {
            SortingOrder.ASCENDING -> when(sortingType) {
                SortingType.CALLSIGN -> mapItems.sortBy(SORT_ALPHA)
                SortingType.TIME -> mapItems.sortBy(SORT_TIME)
                SortingType.TEAM -> mapItems.sortBy(SORT_TEAM)
                SortingType.ROLE -> mapItems.sortBy(SORT_ROLE)
            }
            SortingOrder.DESCENDING -> when(sortingType) {
                SortingType.CALLSIGN -> mapItems.sortByDescending(SORT_ALPHA)
                SortingType.TIME -> mapItems.sortByDescending(SORT_TIME)
                SortingType.TEAM -> mapItems.sortByDescending(SORT_TEAM)
                SortingType.ROLE -> mapItems.sortByDescending(SORT_ROLE)
            }
        }
        notifyDataSetChanged()
    }

    private fun addItems(group: MapGroup) {
        mapItems.addAll(
            group.items.filter { it.hasMetaValue(CotMetadata.ROLE) }
        )
        group.childGroups.forEach { addItems(it) }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val index: TextView = view.findViewById(R.id.index)
        val icon: ImageView = view.findViewById(R.id.icon)
        val callsign: TextView = view.findViewById(R.id.callsign)
        val lastUpdate: TextView = view.findViewById(R.id.last_update)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition < 0 || adapterPosition >= itemCount) return
            MapTouchController.goTo(
                mapItems[adapterPosition],
                SELECT_MAP_ITEM_ON_CLICK
            )
        }
    }

    private companion object {
        /* When we click a list item, go to that item on the map and open its radial menu */
        const val SELECT_MAP_ITEM_ON_CLICK = true

        /* Some pre-constructed sorting schemas, to be called in sortBy() */
        val SORT_ALPHA: (MapItem) -> String = { it.getMetaString(CotMetadata.CALLSIGN, "") }
        val SORT_TIME: (MapItem) -> Long = { it.getMetaLong(CotMetadata.LAST_UPDATE, 0L) }
        val SORT_TEAM: (MapItem) -> Int = { it.getMetaInteger(CotMetadata.TEAM, 0) }
        val SORT_ROLE: (MapItem) -> String = { it.getMetaString(CotMetadata.CALLSIGN, "") }
    }
}
