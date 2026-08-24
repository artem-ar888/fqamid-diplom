package ru.edu.qamid.utils.data;

import ru.edu.qamid.test.BuildConfig;

public class TestData {
    public static final String LOGIN = "login2";
    public static final String PASSWORD = "password2";
    public static final String USER_LOGIN_PREFIX = "null_user_";
    public static final String WRONG_PASSWORD = "wrong_pw";
    public static final String BASE_URL = BuildConfig.BASE_URL;
//    public static final String BASE_URL = "https://students.netoservices.ru/qamid-diplom-backend/";

    // Метод возвращает строку с пробелами с обеих сторон
    public static String loginWithBothSpaces() {
        return "   " + LOGIN + "   ";
    }

    public static String passwordWithBothSpaces() {
        return "   " + PASSWORD + "   ";
    }

    public static final int PAGE_SIZE = 8;
    public static final long MINUTE = 60L;
    public static final long HOUR = 60L * MINUTE; // 3600
    public static final long DAY = 24L * HOUR; // 86400
    public static final long WEEK = 7L * DAY; // 604800


}
