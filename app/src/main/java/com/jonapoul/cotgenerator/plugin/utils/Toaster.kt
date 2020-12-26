package com.jonapoul.cotgenerator.plugin.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object Toaster {
    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun toast(context: Context, @StringRes textId: Int) {
        toast(context, context.getString(textId))
    }
}
