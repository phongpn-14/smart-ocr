package com.example.smartocr.util

import java.lang.ref.WeakReference

/**
 * Used as a wrapper for data that is exposed via a LiveData, Flow,... that represents an event.
 */
open class Event<out T>(private val content: T) {

    private var consumers = mutableListOf<WeakReference<Any>>()
    fun getValueIfNotHandle(consumer: Any?): T? {
        var isConsumerExisted = false
        consumers.forEach {
            if (it.get() == consumer) {
                isConsumerExisted = true
                return@forEach
            }
        }
        return if (isConsumerExisted) null else {
            consumers.add(WeakReference(consumer))
            content
        }
    }

    fun getValueIfNotHandle(consumer: Any? = null, resultNotNull: (T) -> Unit) {
        getValueIfNotHandle(consumer)?.let { resultNotNull(it) }
    }


    fun value(): T = content

    val value get() = content
}

fun<T: Any?> singleEventOf(data: T) = Event(data)
fun<T: Any?> T.event() = Event(this)


