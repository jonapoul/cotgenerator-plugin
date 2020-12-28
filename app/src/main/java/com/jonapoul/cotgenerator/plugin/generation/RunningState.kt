package com.jonapoul.cotgenerator.plugin.generation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jonapoul.cotgenerator.plugin.R
import timber.log.Timber

enum class RunningState(
    @StringRes val textId: Int,
    @DrawableRes val drawableId: Int
) {
    RUNNING(
        R.string.home_button_stop,
        R.drawable.stop
    ),
    STOPPED(
        R.string.home_button_start,
        R.drawable.start
    );

    companion object {
        private var currentState = STOPPED

        fun setState(runningState: RunningState) {
            synchronized(currentState) {
                Timber.i("setState $runningState")
                currentState = runningState
            }
        }

        fun getState(): RunningState {
            synchronized(currentState) {
                Timber.i("getState $currentState")
                return currentState
            }
        }
    }
}
