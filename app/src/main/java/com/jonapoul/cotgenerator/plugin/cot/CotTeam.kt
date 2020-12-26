package com.jonapoul.cotgenerator.plugin.cot

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getBooleanFromPair
import com.jonapoul.sharedprefs.getStringFromPair
import java.util.*

@Suppress("SpellCheckingInspection")
enum class CotTeam(val desc: String, val hex: String) {
    PURPLE("Purple", "FF800080"),
    MAGENTA("Magenta", "FFFF00FF"),
    MAROON("Maroon", "FF800000"),
    RED("Red", "FFFF0000"),
    ORANGE("Orange", "FFFF8000"),
    YELLOW("Yellow", "FFFFFF00"),
    WHITE("White", "FFFFFFFF"),
    GREEN("Green", "FF008009"),
    DARK_GREEN("Dark Green", "FF00620B"),
    CYAN("Cyan", "FF00FFFF"),
    TEAL("Teal", "FF008784"),
    BLUE("Blue", "FF0003FB"),
    DARK_BLUE("Dark Blue", "FF0000A0");

    override fun toString() = desc

    fun toInt() = Integer.parseInt(hex.substring(2), 16)

    companion object {
        private val random = Random()

        fun fromName(name: String): CotTeam {
            return values().firstOrNull { it.desc == name }
                ?: throw IllegalArgumentException("Unknown CoT team: $name")
        }

        private fun fromHexString(hexString: String): CotTeam {
            return values().firstOrNull { it.hex == hexString }
                ?: throw IllegalArgumentException("Unknown CoT team: $hexString")
        }

        @SuppressLint("DefaultLocale")
        fun fromPrefs(prefs: SharedPreferences): CotTeam {
            val isRandom = prefs.getBooleanFromPair(Prefs.USE_RANDOM_TEAM_COLOURS)
            return if (isRandom) {
                values()[random.nextInt(values().size)]
            } else {
                val value = prefs.getStringFromPair(Prefs.TEAM_COLOUR)
                values().find { it.desc == value }
                    ?: throw IllegalArgumentException("Unknown CoT team: $value")

            }
        }
    }
}
