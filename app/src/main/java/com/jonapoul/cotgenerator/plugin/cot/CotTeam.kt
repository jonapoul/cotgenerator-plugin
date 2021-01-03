package com.jonapoul.cotgenerator.plugin.cot

import android.content.SharedPreferences
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.getStringFromPair
import java.util.*

@Suppress("SpellCheckingInspection")
enum class CotTeam(private val colour: String) {
    PURPLE("Purple"),
    MAGENTA("Magenta"),
    MAROON("Maroon"),
    RED("Red"),
    ORANGE("Orange"),
    YELLOW("Yellow"),
    WHITE("White"),
    GREEN("Green"),
    DARK_GREEN("Dark Green"),
    CYAN("Cyan"),
    TEAL("Teal"),
    BLUE("Blue"),
    DARK_BLUE("Dark Blue");

    override fun toString() = colour

    companion object {
        private val random = Random()

        fun fromPrefs(prefs: SharedPreferences): CotTeam {
            val isRandom = prefs.getBooleanFromPair(Prefs.USE_RANDOM_TEAMS)
            return if (isRandom) {
                values()[random.nextInt(values().size)]
            } else {
                val value = prefs.getStringFromPair(Prefs.TEAM)
                values().find { it.colour == value }
                    ?: throw IllegalArgumentException("Unknown CoT team: $value")

            }
        }
    }
}
