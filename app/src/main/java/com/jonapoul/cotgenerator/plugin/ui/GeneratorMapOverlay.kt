package com.jonapoul.cotgenerator.plugin.ui

import android.widget.BaseAdapter
import com.atakmap.android.hierarchy.HierarchyListFilter
import com.atakmap.android.hierarchy.HierarchyListItem
import com.atakmap.android.maps.DeepMapItemQuery
import com.atakmap.android.maps.MapGroup
import com.atakmap.android.overlay.AbstractMapOverlay2

class GeneratorMapOverlay : AbstractMapOverlay2() {
    override fun getIdentifier(): String = GeneratorMapOverlay::class.java.simpleName

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getRootGroup(): MapGroup {
        TODO("Not yet implemented")
    }

    override fun getQueryFunction(): DeepMapItemQuery {
        TODO("Not yet implemented")
    }

    override fun getListModel(p0: BaseAdapter?, p1: Long, p2: HierarchyListFilter?): HierarchyListItem {
        TODO("Not yet implemented")
    }
}
