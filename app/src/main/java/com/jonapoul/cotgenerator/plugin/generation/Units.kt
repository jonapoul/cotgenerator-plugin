package com.jonapoul.cotgenerator.plugin.generation

internal typealias Radians = Double
internal typealias Degrees = Double

internal fun Degrees.toRadians(): Radians = this / 180.0 * Math.PI
internal fun Radians.toDegrees(): Degrees = this * 180.0 / Math.PI