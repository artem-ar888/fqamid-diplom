package ru.edu.qamid.utils.data;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.security.SecureRandom;
import java.time.format.DateTimeParseException;

import ru.edu.qamid.R;

public final class DataGenerator {

    private DataGenerator() { }
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz013456789";
    private static final String SPECIALS = "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter FORMAT_FULL =
            DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm:ss");

    private static final DateTimeFormatter FORMAT_NO_SECONDS =
            DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");

    private static final DateTimeFormatter FORMAT_DATE_ONLY =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Генерирует рандомное число в указанном диапазоне
     */
    public static int randomIntBetween(int minInclusive, int maxInclusive) {
        if (minInclusive >= maxInclusive) {
            throw new IllegalArgumentException("Max must be greater than Min");
        }
        return RANDOM.nextInt((maxInclusive - minInclusive) + 1) + minInclusive;
    }

    /**
     * Случайная строка фиксированной длины из букв и цифр
     */
    public static String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHANUM.length());
            sb.append(ALPHANUM.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Случайная строка со спецсимволами (для тестов на SQL-инъекции или XSS)
     */
    public static String randomWithSpecialChars(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(SPECIALS.length());
            sb.append(SPECIALS.charAt(index));
        }
        return sb.toString();
    }

    /* =========================================== */
    /* ВРЕМЕННЫЕ МЕТКИ (TIMESTAMPS)                */
    /* =========================================== */

    /**
     * Генерирует Unix Timestamp относительно текущего момента по UTC.
     */
    public static long generateRelativeUnixTimestampSeconds(long secondsFromNow) {
        return Instant.now()
                .plusSeconds(secondsFromNow)
                .getEpochSecond();
    }

    /**
     * Текущее время в формате Unix Timestamp (секунды).
     */
    public static long generateCurrentUnixTimestamp() {
        return generateRelativeUnixTimestampSeconds(0);
    }

    public static String generateDateStringDaysAgo(int daysAgo) {
        // 1. daysAgo передаем как отрицательное число, чтобы вычесть дни из текущего момента
        long secondsFromNow = (long) -1 * daysAgo * TestData.DAY;

        // 2. Получаем Unix Timestamp (в секундах) с помощью твоего метода
        long unixTimestamp = generateRelativeUnixTimestampSeconds(secondsFromNow);

        // 3. Конвертируем Unix Timestamp (секунды) обратно в LocalDate по UTC
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        LocalDate date = instant.atZone(ZoneOffset.UTC).toLocalDate();
        // LocalDateTime dateTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime();

        // 4. Форматируем в строку "ДД.ММ.ГГГГ"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy_HH:mm:ss");
        return date.format(formatter);
        // return dateTime.format(formatter);
    }

    public static String generateDateStringDaysFuture(int daysFuture) {
        return generateDateStringDaysAgo(-daysFuture);
    }

    /**
     * Генерирует строку времени "ЧЧ:ММ" со смещением от текущего момента.
     *
     * @param minutesOffset Смещение в минутах. Может быть:
     *                      - положительным (будущее),
     *                      - отрицательным (прошлое),
     *                      - нулём (сейчас).
     * @return Время в формате "ЧЧ:ММ" (24‑часовой формат, с ведущим нулём, например "09:05")
     */
    public static String generateTimeStringMinutesOffset(int minutesOffset) {
        // 1. Считаем смещение в секундах
        long secondsFromNow = (long) minutesOffset * TestData.MINUTE;

        // 2. Получаем Unix-timestamp
        long unixTimestamp = generateRelativeUnixTimestampSeconds(secondsFromNow);

        // 3. Конвертируем в момент времени
        Instant instant = Instant.ofEpochSecond(unixTimestamp);

        // 4. Превращаем в время
        LocalTime time = instant.atZone(ZoneOffset.UTC).toLocalTime();

        // 5. Форматируем
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    /**
     * Возвращает текущее время в виде строки "ddMMyy_HHmmss" (использует системное время).
     * Осторожно: зависит от локального времени машины!
     */
    public static String generateLocalTimestampString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy_HHmmss"));
    }

    /**
     * Генерирует строку вида: [prefix][timestamp_in_ddMMyy_HHmmss]
     */
    public static String generateWithTimestamp(String prefix) {
        return prefix + generateLocalTimestampString();
    }

    public static String generateWithTimestamp(String prefix, String suffix) {
        return prefix + generateLocalTimestampString() + suffix;
    }

    /**
     * Генерирует строку вида: [prefix][unix_timestamp_seconds]
     */
    public static String generateWithUnixTimestamp(String prefix) {
        return prefix + generateCurrentUnixTimestamp();
    }

    /* =========================================== */
    /* ПАРСИНГ СТРОК                               */
    /* =========================================== */

    /**
     * Конвертирует строку даты в Unix Timestamp (секунды UTC) с ручным указанием часового пояса.
     * ВАЖНО: Параметр {@code hoursFromUtc} определяет разницу локального времени С НУЛЕВЫМ ПОЯСОМ (UTC).
     * Примеры:
     * Москва (MSK, UTC+3)  -> передавайте 3
     * Берлин (CET, UTC+1)  -> передавайте 1
     * Нью-Йорк (EST, UTC-5) -> передавайте -5
     *
     * @param dateStr Строка даты в формате dd.MM.yyyy[_HH:mm[:ss]].
     * @param hoursFromUtc Смещение локального времени относительно UTC в часах.
     *                     Положительное значение для восточных поясов (к востоку от Гринвича),
     *                     отрицательное — для западных.
     * @return Количество секунд с 1 января 1970 года (Unix Epoch) в шкале UTC.
     */
    public static long dateStringToUtcUnixTimestamp(String dateStr, int hoursFromUtc) throws IOException {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty.");
        }

        String cleanInput = dateStr.trim();

        try {
            DateTimeFormatter formatter;
            LocalDateTime ldt;

            switch (cleanInput.length()) {
                case 19: // "dd.MM.yyyy_HH:mm:ss"
                    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm:ss");
                    ldt = LocalDateTime.parse(cleanInput, formatter);
                    break;

                case 16: // "dd.MM.yyyy_HH:mm"
                    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
                    ldt = LocalDateTime.parse(cleanInput, formatter);
                    break;

                case 10: // "dd.MM.yyyy" (только дата)
                    return LocalDate.parse(cleanInput, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                            .atStartOfDay(ZoneOffset.ofHours(hoursFromUtc)) // Применяем пояс сразу
                            .toEpochSecond();

                default:
                    throw new IllegalArgumentException(
                            "Unsupported date format length for string: '" + dateStr + "'");
            }

            // Алгоритм расчета:
            // Метод ZoneOffset.ofHours() ожидает "смещение ОТНОСИТЕЛЬНО UTC".
            // Для пояса MSK (UTC+3) мы передаем число 3.
            // Вызов .toInstant(ZoneOffset.UTC_PLUS_3) говорит Java:
            // "Эти цифры времени даны для пояса +3. Пересчитай их в абсолютный момент UTC".
            // Поскольку Instant хранит UTC, прибавление положительного смещения (+3)
            // фактически сдвигает время НАЗАД к нулевому меридиану.
            return ldt.toInstant(ZoneOffset.ofHours(hoursFromUtc)).getEpochSecond();

        } catch (DateTimeParseException e) {
            throw new IOException("Failed to parse date string: '" + dateStr + "'. Check the format.", e);
        }
    }

    /**
     * Перегрузка для удобства: если пояс не указан, считаем что это Москва (+3).
     */
    public static long dateStringToUtcUnixTimestamp(String dateStr) throws IOException {
        return dateStringToUtcUnixTimestamp(dateStr, 3); // По умолчанию MSK
    }

    /**
     * Конвертирует ID категории новости в человекочитаемое название.
     * Используется в тестах и логах.
     */
    public static String getCategoryName(int categoryId) {
        return switch (categoryId) {
            case 1 -> "Объявление";
            case 2 -> "День рождения";
            case 3 -> "Зарплата";
            case 4 -> "Профсоюз";
            case 5 -> "Праздник";
            case 6 -> "Массаж";
            case 7 -> "Благодарность";
            case 8 -> "Нужна помощь";
            default -> throw new IllegalArgumentException("Unknown categoryId: " + categoryId);
        };
    }

    public static int getIconResIdForCategory(String categoryName) {
        if (categoryName == null) {
            throw new IllegalArgumentException("Category name cannot be null");
        }
        return switch (categoryName) {
            case "Advertisement" -> R.raw.icon_advertisement;
            case "Birthday" -> R.raw.icon_birthday;
            case "Salary" -> R.raw.icon_salary;
            case "Union" -> R.raw.icon_union;
            case "Holiday" -> R.raw.icon_holiday;
            case "Massage" -> R.raw.icon_massage;
            case "Gratitude" -> R.raw.icon_gratitude;
            case "Help" -> R.raw.icon_help;
            default -> throw new IllegalArgumentException(
                    "Unknown category: " + categoryName +
                            ". Available: Massage, Salary, Advertisement, Union..."
            );
        };
    }

    public static String transliterate(String message) {
        if (message == null) return "";

        // Сначала заменяем ёлочки на стандартные ASCII-кавычки
        message = message.replace("«", "'").replace("»", "'");

        // Массив русских букв и их аналогов на латинице
        String[] abcRu = {
                "а","б","в","г","д","е","ё","ж","з","и","й","к","л","м","н","о","п","р","с","т",
                "у","ф","х","ц","ч","ш","щ","ъ","ы","ь","э","ю","я",
                "А","Б","В","Г","Д","Е","Ё","Ж","З","И","Й","К","Л","М","Н","О","П","Р","С","Т",
                "У","Ф","Х","Ц","Ч","Ш","Щ","Ъ","Ы","Ь","Э","Ю","Я"
        };
        String[] abcEn = {
                "a","b","v","g","d","e","yo","zh","z","i","y","k","l","m","n","o","p","r","s","t",
                "u","f","h","ts","ch","sh","shch","","y","","eh","yu","ya",
                "A","B","V","G","D","E","Yo","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T",
                "U","F","H","Ts","Ch","Sh","Shch","","Y","","Eh","Yu","Ya"
        };

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            String letter = String.valueOf(message.charAt(i));
            boolean found = false;
            for (int j = 0; j < abcRu.length; j++) {
                if (letter.equals(abcRu[j])) {
                    builder.append(abcEn[j]);
                    found = true;
                    break;
                }
            }
            if (!found) {
                builder.append(letter); // Оставляем пробелы, знаки препинания и цифры как есть
            }
        }
        return builder.toString();
    }
}
