package com.jonapoul.cotgenerator.plugin.ui

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jonapoul.cotgenerator.plugin.BuildConfig
import com.jonapoul.cotgenerator.plugin.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Base class for displaying the version information of the app. This indicates the build version
 * name (e.g. "1.2.0"), build type (e.g. "Release") and build time (e.g. "15:31:46 02 Dec 2020 GMT").
 * Also includes an external link to the plugin's GitHub repo page.
 */
internal class AboutDialog(
    private val appContext: Context,
    private val pluginContext: Context
) : AlertDialog.Builder(appContext) {

    private data class Row(
        @StringRes val titleId: Int,
        val subtitle: String,
        @DrawableRes var iconId: Int? = null
    )

    private val buildVersion = BuildConfig.VERSION_NAME
    private val buildTimestamp = SimpleDateFormat("HH:mm:ss dd MMM yyyy z", Locale.ENGLISH)
        .format(BuildConfig.BUILD_TIMESTAMP)
    private val githubRepository = "https://github.com/jonapoul/cotgenerator-plugin"

    override fun show(): AlertDialog {
        val rows: List<Row> = listOf(
            Row(R.string.about_version, buildVersion),
            Row(R.string.about_build_time, buildTimestamp),
            Row(R.string.about_git_repo, githubRepository, R.drawable.go_to),
        )

        val rootView = View.inflate(pluginContext, R.layout.about_dialog, null)
        val listView = rootView.findViewById<ListView>(R.id.list_view)
        listView.adapter = object : ArrayAdapter<Row>(pluginContext, ROW_LAYOUT, R.id.row_title, rows) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, null, parent).also {
                    val row = rows[position]
                    it.findViewById<TextView>(R.id.row_title).text = pluginContext.getString(row.titleId)
                    it.findViewById<TextView>(R.id.row_subtitle).text = row.subtitle
                    if (row.iconId != null) {
                        it.findViewById<ImageView>(R.id.row_icon).visibility = View.VISIBLE
                        it.setOnClickListener { launchGithubRepo() }
                    }
                }
            }
        }
        setTitle(pluginContext.getString(R.string.about_title))
        setPositiveButton(android.R.string.ok, null)
        setView(listView)

        return super.show()
    }

    private fun launchGithubRepo() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(githubRepository)
        try {
            appContext.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toaster.toast(appContext, R.string.about_no_compatible_apps)
        }
    }

    private companion object {
        const val ROW_LAYOUT = R.layout.about_dialog_row
    }
}