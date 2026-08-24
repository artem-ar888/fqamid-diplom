package ru.edu.qamid.utils.helpers;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static org.hamcrest.Matchers.equalTo;

import android.view.View;

import androidx.test.espresso.matcher.RootMatchers;

import org.hamcrest.Matcher;

public class AutoCompleteHelper {
    /**
     * Вариант 1: вводимая строка = тексту пункта в списке.
     * Пример: selectCategory(autoComplete, "Зарплата")
     */
    public static void selectCategory(Matcher<View> autoCompleteMatcher, String category) {
        selectCategory(autoCompleteMatcher, category, category);
    }

    /**
     * Вариант 2: отдельно «что печатаем» и отдельно «какой пункт выбираем».
     * Полезно, если в списке пункты имеют более длинные/форматированные названия.
     * Пример: selectCategory(autoComplete, "п", "Нужна помощь")
     */
    public static void selectCategory(
            Matcher<View> autoCompleteMatcher,
            String inputText,
            String expectedListItemText
    ) {
        // 1. Фокус и ввод текста
        onView(autoCompleteMatcher)
                .perform(click(), replaceText(inputText), closeSoftKeyboard());

        // 2. Ждём появления выпадающего списка и кликаем по нужному элементу
        onData(equalTo(expectedListItemText))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
    }
}

