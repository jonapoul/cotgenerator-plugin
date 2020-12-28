package com.jonapoul.cotgenerator.plugin.generation.streams

interface IRandomStream<T> {
    fun next(): T
}
