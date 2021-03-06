package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.android.maps.MapView
import com.atakmap.comms.CotDispatcher
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getIntFromPair
import timber.log.Timber

internal class GeneratorRunnable(
    private val mapView: MapView,
    private val prefs: SharedPreferences,
    private val factory: CotEventFactory,
    private val dispatcher: CotDispatcher
) : Runnable {

    @Volatile private var isRunning = false

    override fun run() {
        try {
            Timber.i("Running")
            isRunning = true
            val startNs = System.nanoTime()
            val cotEvents = factory.generate()
            val sleepTimePerDispatch = generationPeriodMs(startNs) / cotEvents.size
            DrawCircleRunnable(mapView, prefs).run()
            for (event in cotEvents) {
                if (!isRunning) break
                dispatcher.dispatch(event)
                bufferSleep(sleepTimePerDispatch)
            }
        } catch (e: Exception) {
            Timber.w(e)
        }
        Timber.i("Finishing runnable")
    }

    fun stop() {
        isRunning = false
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
        val updatePeriodMs = prefs.getIntFromPair(Prefs.PERIOD) * 1000L
        return updatePeriodMs - generationTimeMs
    }
}
