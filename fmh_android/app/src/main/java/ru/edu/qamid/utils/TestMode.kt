package ru.edu.qamid.utils

object TestMode {
    private var isActive = false

    @JvmStatic
    fun start() {
        isActive = true
        android.util.Log.d("TEST_DEBUG", "TestMode: STARTED")
    }

    @JvmStatic
    fun stop() {
        isActive = false
        android.util.Log.d("TEST_DEBUG", "TestMode: STOPPED")
    }

    /**
     * Возвращает true, если сейчас выполняется Espresso-тест.
     */
    @JvmStatic
    fun isRunning(): Boolean {
        val result = isActive
        android.util.Log.d(
            "TEST_DEBUG",
            "TestMode.isRunning() = $result"
        )
        return result
    }
}