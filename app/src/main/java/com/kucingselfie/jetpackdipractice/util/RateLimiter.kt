package com.kucingselfie.jetpackdipractice.util

import android.os.SystemClock
import android.util.ArrayMap
import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
class RateLimiter<in KEY>(timeout: Int, timeunit: TimeUnit) {
    private val timeStamps = ArrayMap<KEY, Long>()
    private val timeout = timeunit.toMillis(timeout.toLong())

    @Synchronized
    fun shouldFetch(key: KEY): Boolean {
        val lastFetched = timeStamps[key]
        val now = now()
        if (lastFetched == null) {
            timeStamps[key] = now
            return true
        }
        if (now - lastFetched > timeout) {
            timeStamps[key] = now
            return true
        }
        return false
    }

    private fun now() = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: KEY) {
        timeStamps.remove(key)
    }
}