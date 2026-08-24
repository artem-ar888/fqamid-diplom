package ru.edu.qamid.utils

object TestContext {

    @JvmStatic
    fun isRunningInEspressoTest(): Boolean {
        return TestMode.isRunning()
    }
}