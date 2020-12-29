package com.jonapoul.cotgenerator.plugin.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.atakmap.android.gui.PanEditTextPreference
import com.atakmap.android.gui.PanPreference
import com.jonapoul.cotgenerator.plugin.R
import timber.log.Timber

class RefreshCallsignPreference @JvmOverloads constructor(
    appContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextPreferenceStyle,
    defStyleRes: Int = 0,
) : PanEditTextPreference(appContext, attrs, defStyleAttr, defStyleRes) {

    init {
        isPersistent = true
        PanPreference.setup(attrs, appContext, this, hashMapOf())
    }

    override fun onCreateView(parent: ViewGroup?): View {
        /* This super call is being ignored because we don't want to use the inflated layout
         * resource from Android proper, we have a slightly altered one in the plugin's resources
         * instead. So here we call the super-method only because it's a restriction of the class,
         * then ignore the inflated view and inflate our own instead. */
        /*val ignored =*/ super.onCreateView(parent)
        Timber.i("onCreateView")
        val inflater = LayoutInflater.from(pluginContext)
        val view = inflater.inflate(R.layout.refresh_callsign_pref_layout, parent, false)
        view.findViewById<ViewGroup>(android.R.id.widget_frame).also {
            inflater.inflate(R.layout.refresh_callsign_pref_widget, it)
            it.visibility = View.VISIBLE
        }
        return view
    }

    override fun onBindView(view: View) {
        super.onBindView(view)
        Timber.i("onBindView")
        view.findViewById<Button>(R.id.refresh_button).setOnClickListener {
            val newCallsign = atakCallsigns?.random()
            prefs!!.edit().putString(Keys.BASE_CALLSIGN, newCallsign).apply()
        }
    }

    companion object {
        private var pluginContext: Context? = null
        private var atakCallsigns: Array<String>? = null
        private var prefs: SharedPreferences? = null

        fun setResources(pluginContext: Context, appContext: Context) {
            this.pluginContext = pluginContext
            atakCallsigns = pluginContext.resources.getStringArray(R.array.callsigns)
            prefs = PreferenceManager.getDefaultSharedPreferences(appContext)
        }
    }
}
