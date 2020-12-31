package com.jonapoul.cotgenerator.plugin.generation

import androidx.annotation.DrawableRes
import com.jonapoul.cotgenerator.plugin.R
import timber.log.Timber

enum class RunningState(@DrawableRes val widgetDrawableId: Int) {
    RUNNING(R.drawable.plugin_icon_green),
    STOPPED(R.drawable.plugin_icon_red);

    interface StateListener {
        fun onRunningStateChanged(newState: RunningState)
    }

    companion object {
        private var currentState = STOPPED
        private val listeners = mutableSetOf<StateListener>()

        fun setState(runningState: RunningState) {
            synchronized(currentState) {
                Timber.i("setState $runningState")
                currentState = runningState
                listeners.forEach { it.onRunningStateChanged(runningState) }
            }
        }

        fun getState(): RunningState {
            synchronized(currentState) {
                Timber.i("getState $currentState")
                return currentState
            }
        }

        fun addStateListener(listener: StateListener) {
            listeners.add(listener)
        }

        fun removeListener(listener: StateListener) {
            listeners.remove(listener)
        }
    }
}
