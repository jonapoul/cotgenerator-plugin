package com.jonapoul.cotgenerator.plugin.generation

import android.content.SharedPreferences
import com.atakmap.android.maps.MapView
import com.atakmap.comms.CotDispatcher
import com.atakmap.comms.DispatchFlags
import com.jonapoul.cotgenerator.plugin.prefs.Prefs
import com.jonapoul.sharedprefs.parseIntFromPair
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class GeneratorThreadManager private constructor() {

    private val lock = Any()
    private lateinit var runnable: GeneratorRunnable
    private lateinit var executor: ScheduledExecutorService
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
            val periodSeconds = prefs.parseIntFromPair(Prefs.UPDATE_PERIOD).toLong()

            runnable = GeneratorRunnable(
                prefs,
                factory,
                dispatcher,
                DrawCircleRunnable(
                    mapView,
                    prefs,
                    DrawCircleRunnable.Mode.DRAW
                )
            )
            future = executor.scheduleAtFixedRate(
                runnable,
                INITIAL_DELAY,
                periodSeconds,
                TimeUnit.SECONDS
            )
        }
    }

    fun stop(mapView: MapView, prefs: SharedPreferences) {
        synchronized(lock) {
            Timber.i("stop")
            runnable.stop()
            future?.cancel(true)
            executor.shutdownNow()
            DrawCircleRunnable(mapView, prefs, DrawCircleRunnable.Mode.DELETE).run()
        }
    }

    companion object {
        fun getInstance() = instance

        private val instance = GeneratorThreadManager()

        private const val INITIAL_DELAY = 0L
    }
}
