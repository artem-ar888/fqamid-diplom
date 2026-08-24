package ru.edu.qamid.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL_ESPRESSO_IDLING_RESOURCE"
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    @JvmStatic
    fun increment() {
        countingIdlingResource.increment()
    }

    @JvmStatic
    fun decrement() {
        countingIdlingResource.decrement()
    }
}