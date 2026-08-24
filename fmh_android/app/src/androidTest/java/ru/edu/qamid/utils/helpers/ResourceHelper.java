package ru.edu.qamid.utils.helpers;

import androidx.test.platform.app.InstrumentationRegistry;

public final class ResourceHelper {

    private ResourceHelper() {
    }

    /**
     * Возвращает строку из resources по ID.
     */
    public static String getString(int resId) {
        return InstrumentationRegistry.getInstrumentation()
                .getTargetContext()
                .getString(resId);
    }

    /**
     * Форматированная строка (если есть плейсхолдеры %s).
     */
    public static String getString(int resId, Object... formatArgs) {
        return InstrumentationRegistry.getInstrumentation()
                .getTargetContext()
                .getString(resId, formatArgs);
    }
}