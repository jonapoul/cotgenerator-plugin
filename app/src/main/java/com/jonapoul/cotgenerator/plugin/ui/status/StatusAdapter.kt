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
import com.atakmap.android.math.MathUtils
import com.atakmap.android.util.ATAKUtilities
import com.atakmap.coremap.maps.time.CoordinatedTime
import com.jonapoul.cotgenerator.plugin.R
import com.jonapoul.cotgenerator.plugin.utils.CotMetadata


internal class StatusAdapter(
    private val pluginContext: Context,
    mapView: MapView,

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
        holder.lastUpdate.text = MathUtils.GetTimeRemainingOrDateString(
            now,
            elapsed,
            SHOW_OLD_TIMES_AS_DATE
        )
    }

    override fun getItemCount(): Int = mapItems.size

    fun addItem(item: MapItem) {
        mapItems.add(item)
        notifyDataSetChanged()
    }

    fun removeItem(item: MapItem) {
        mapItems.remove(item)
        notifyDataSetChanged()
    }

    fun sortBy(sortingType: StatusSortingType) {
        when (sortingType) {
            StatusSortingType.ALPHABET ->
                mapItems.sortBy { it.getMetaString(CotMetadata.CALLSIGN, "") }
            StatusSortingType.TIME ->
                mapItems.sortBy { it.getMetaLong(CotMetadata.LAST_UPDATE, 0) }
            StatusSortingType.TEAM ->
                mapItems.sortBy { it.getMetaInteger(CotMetadata.TEAM, 0) }
            StatusSortingType.ROLE ->
                mapItems.sortBy { it.getMetaString(CotMetadata.ROLE, "") }
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
        /* If the map item is old, display as "1 week ago" instead of "2020-12-01" or whatever */
        const val SHOW_OLD_TIMES_AS_DATE = false

        /* When we click a list item, go to that item on the map and open its radial menu */
        const val SELECT_MAP_ITEM_ON_CLICK = true
    }
}
