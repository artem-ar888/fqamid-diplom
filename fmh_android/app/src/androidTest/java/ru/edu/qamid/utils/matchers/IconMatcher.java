package ru.edu.qamid.utils.matchers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ru.edu.qamid.R;

public class IconMatcher {
    // Фиксированный размер для сравнения.
    // Не зависит от устройства, не зависит от layout.
    private static final int TARGET_SIZE_PX = 48;

    private static final Map<Bitmap, String> CATEGORY_MAP = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Вызови этот метод в @Before твоего BaseE2eEspressoTest
     */
    public static void initCategoryMap(Context context) {
        if (initialized) return;

        loadCategory(context, R.raw.icon_advertisement, "Advertisement");
        loadCategory(context, R.raw.icon_birthday, "Birthday");
        loadCategory(context, R.raw.icon_salary, "Salary");
        loadCategory(context, R.raw.icon_union, "Union");
        loadCategory(context, R.raw.icon_holiday, "Holiday");
        loadCategory(context, R.raw.icon_massage, "Massage");
        loadCategory(context, R.raw.icon_gratitude, "Gratitude");
        loadCategory(context, R.raw.icon_help, "Help");

        initialized = true;
    }

    private static void loadCategory(Context context, int rawResId, String categoryName) {
        try (InputStream is = context.getResources().openRawResource(rawResId)) {
            Bitmap original = BitmapFactory.decodeStream(is);
            if (original == null) {
                System.out.println("WARN: Failed to load icon for " + categoryName);
                return;
            }
            // Ресайзим эталон до фиксированного размера сразу при загрузке
            Bitmap resized = Bitmap.createScaledBitmap(original, TARGET_SIZE_PX, TARGET_SIZE_PX, true);
            CATEGORY_MAP.put(resized, categoryName);
        } catch (Exception e) {
            System.out.println("WARN: Error loading icon for " + categoryName + ": " + e.getMessage());
        }
    }

    /**
     * Автоматически определяет категорию по Bitmap иконки
     */
    public static String detectCategoryFromBitmap(Bitmap iconBitmap) {
        if (iconBitmap == null) return null;

        // Приводим картинку из UI к тому же фиксированному размеру
        Bitmap normalized = Bitmap.createScaledBitmap(iconBitmap, TARGET_SIZE_PX, TARGET_SIZE_PX, true);

        for (Map.Entry<Bitmap, String> entry : CATEGORY_MAP.entrySet()) {
            if (normalized.sameAs(entry.getKey())) {
                return entry.getValue();
            }
        }

        System.out.println("WARN: Icon did not match any of the 8 categories.");
        return null;
    }
}
