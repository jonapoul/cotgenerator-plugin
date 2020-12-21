package com.jonapoul.cotgenerator.plugin.utils

import java.util.concurrent.TimeUnit


object TimeUtils {
    /**
     * Converts a time difference in milliseconds into a human-readable string, to show how
     * long ago something happened.
     */
    fun lastHeardTime(milliseconds: Long): String {
        if (milliseconds < 0) return "0s ago"

        val days = TimeUnit.MILLISECONDS.toDays(milliseconds)
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds) % 24
        val mins = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val secs = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60

        return when {
            days >= 1 -> "${days}d ${hours}h ${mins}m ${secs}s ago"
            hours >= 1 -> "${hours}h ${mins}m ${secs}s ago"
            mins >= 1 -> "${mins}m ${secs}s ago"
            else -> "${secs}s ago"
        }
    }

    private const val MINUTE = 1000L * 60L
    private const val HOUR = MINUTE * 60L
    private const val DAY = HOUR * 24L
}
