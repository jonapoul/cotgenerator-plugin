package com.jonapoul.cotgenerator.plugin.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.atakmap.android.maps.MapView
import com.jonapoul.cotgenerator.plugin.R
import timber.log.Timber

abstract class HomeScreenView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes),
    SharedPreferences.OnSharedPreferenceChangeListener {

    protected abstract val layoutResource: Int
    protected abstract val titleStringResource: Int

    init {
        inflate(pluginContext, R.layout.home_screen_base, this)
    }

    private val sectionBody: FrameLayout by lazy { findViewById(R.id.section_body) }
    private val toggleButton: ImageView by lazy { findViewById(R.id.toggle_view_button) }

    private var viewState = View.VISIBLE

    protected val mapView: MapView
        get() = mv!!

    protected val pluginContext: Context
        get() = pc!!

    protected val prefs: SharedPreferences
        get() = sp!!

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        prefs.registerOnSharedPreferenceChangeListener(this)

        /* Inflate the sub-view and set it inside the FrameLayout */
        val inflater = LayoutInflater.from(pluginContext)
        sectionBody.removeAllViews()
        sectionBody.addView(
            inflater.inflate(layoutResource, null, false)
        )

        /* Set the title string */
        findViewById<TextView>(R.id.section_header_title).setText(titleStringResource)

        /* Toggle view visibility on button press */
        findViewById<ConstraintLayout>(R.id.section_header).setOnClickListener {
            Timber.i("viewState before = $viewState")
            when (viewState) {
                View.VISIBLE -> {
                    viewState = View.GONE
                    toggleButton.setImageResource(R.drawable.arrow_up)
                }
                View.GONE -> {
                    viewState = View.VISIBLE
                    toggleButton.setImageResource(R.drawable.arrow_down)
                }
            }
            Timber.i("viewState after = $viewState")
            sectionBody.visibility = viewState
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {
        private var mv: MapView? = null
        private var pc: Context? = null
        private var sp: SharedPreferences? = null

        fun setResources(mapView: MapView, pluginContext: Context) {
            mv = mapView
            pc = pluginContext
            sp = PreferenceManager.getDefaultSharedPreferences(mapView.context)
        }
    }
}
