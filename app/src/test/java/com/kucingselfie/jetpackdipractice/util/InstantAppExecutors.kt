package com.kucingselfie.jetpackdipractice.util

import com.kucingselfie.jetpackdipractice.AppExecutors
import java.util.concurrent.Executor

class InstantAppExecutors : AppExecutors(instant, instant, instant) {
    companion object {
        private val instant = Executor { it.run() }
    }
}