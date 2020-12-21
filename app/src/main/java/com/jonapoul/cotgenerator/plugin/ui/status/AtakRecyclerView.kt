package com.jonapoul.cotgenerator.plugin.ui.status

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView override for use with ATAK
 */
internal class AtakRecyclerView : RecyclerView {
    constructor(context: Context)
        : super(context)

    constructor(context: Context, attrs: AttributeSet?)
        : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
        : super(context, attrs, defStyle)

    override fun scrollTo(x: Int, y: Int) {
        /* Not supported and causes a crash when called. Samsung likes to call this method directly
         * outside of our control, so we need to override it with a no-op */
    }
}
