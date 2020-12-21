package com.jonapoul.cotgenerator.plugin.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.XmlRes

@SuppressLint("ValidFragment")
class CallsignPreferenceFragment(
    context: Context = staticContext!!,
    @XmlRes resourceId: Int = staticResourceId!!,
) : BasePreferenceFragment(context, resourceId)
