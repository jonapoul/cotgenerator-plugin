package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.android.maps.MapView
import com.atakmap.comms.CotDispatcher
import com.atakmap.comms.DispatchFlags
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.getIntFromPair
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class GeneratorThreadManager private constructor() {

    private val lock = Any()
    private var runnable: GeneratorRunnable? = null
    private var executor: ScheduledExecutorService? = null
    private var future: ScheduledFuture<*>? = null

    private val dispatcher = CotDispatcher().also {
        it.setDispatchFlags(
            /* Send our CoT data to all sources */
            DispatchFlags.INTERNAL or DispatchFlags.EXTERNAL
        )
    }

    fun start(
        prefs: SharedPreferences,
        mapView: MapView,
        callsigns: List<String>
    ) {
        synchronized(lock) {
            Timber.i("start")
            executor = Executors.newSingleThreadScheduledExecutor()
            val factory = CotEventFactory(mapView, prefs, callsigns)
            val periodSeconds = prefs.getIntFromPair(Prefs.PERIOD).toLong()

            runnable = GeneratorRunnable(
                mapView,
                prefs,
                factory,
                dispatcher
            )
            future = executor?.scheduleAtFixedRate(
                runnable,
                INITIAL_DELAY,
                periodSeconds,
                TimeUnit.SECONDS
            )
        }
    }

    fun stop() {
        synchronized(lock) {
            Timber.i("stop")
            runnable?.stop()
            future?.cancel(true)
            executor?.shutdownNow()
        }
    }

    companion object {
        fun getInstance() = instance

        private val instance = GeneratorThreadManager()

        private const val INITIAL_DELAY = 0L
    }
}
