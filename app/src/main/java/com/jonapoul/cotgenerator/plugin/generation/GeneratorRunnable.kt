package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.comms.CotDispatcher
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.parseIntFromPair
import timber.log.Timber

internal class GeneratorRunnable(
    private val prefs: SharedPreferences,
    private val factory: CotEventFactory,
    private val dispatcher: CotDispatcher
) : Runnable {

    override fun run() {
        try {
            Timber.i("Running")
            val startNs = System.nanoTime()
            val cotEvents = factory.generate()
            val sleepTimePerDispatch = generationPeriodMs(startNs) / cotEvents.size
            cotEvents.forEach {
                dispatcher.dispatch(it)
                bufferSleep(sleepTimePerDispatch)
            }
        } catch (e: Exception) {
            Timber.w(e)
        }
        Timber.i("Finishing runnable!")
    }

    private fun bufferSleep(bufferTimeMs: Long) {
        try {
            Thread.sleep(bufferTimeMs)
        } catch (e: InterruptedException) {
            /* do nothing */
        }
    }

    private fun generationPeriodMs(startNs: Long): Long {
        val generationTimeMs = (System.nanoTime() - startNs) / 1_000_000L
        val updatePeriodMs = prefs.parseIntFromPair(Prefs.UPDATE_FREQUENCY_PER_ICON) * 1000L
        return updatePeriodMs - generationTimeMs
    }
}
