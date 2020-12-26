package com.jonapoul.cotgenerator.plugin.cot

import android.content.SharedPreferences
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.getStringFromPair
import java.util.*

enum class CotRole(private val role: String) {
    TEAM_MEMBER("Team Member"),
    TEAM_LEADER("Team Leader"),
    HQ("HQ"),
    SNIPER("Sniper"),
    MEDIC("Medic"),
    FORWARD_OBSERVER("Forward Observer"),
    RTO("RTO"),
    K9("K9");

    override fun toString() = role

    companion object {
        private val random = Random()

        fun fromString(roleString: String): CotRole {
            return values().firstOrNull { it.toString().equals(roleString, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown CoT role: $roleString")
        }

        fun fromPrefs(prefs: SharedPreferences): CotRole {
            val isRandom = prefs.getBooleanFromPair(Prefs.USE_RANDOM_ROLES)
            return if (isRandom) {
                values()[random.nextInt(values().size)]
            } else {
                fromString(prefs.getStringFromPair(Prefs.ROLE))
            }
        }
    }
}
