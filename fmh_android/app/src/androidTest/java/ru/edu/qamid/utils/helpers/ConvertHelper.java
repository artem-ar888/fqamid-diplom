package ru.edu.qamid.utils.helpers;

public class ConvertHelper {
    /**
     * Валидирует человеческую позицию (>= 1) и переводит её в 0-based индекс.
     */
    public static int checkAndConvertHumanPositionToZeroBased(int humanPosition) {
        if (humanPosition < 1) {
            throw new IllegalArgumentException(
                    "humanPosition must be >= 1. Enter: " + humanPosition
            );
        }
        return humanPosition - 1;
    }
}
