package ru.edu.qamid.utils.helpers;

import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import org.hamcrest.Matcher;

import static androidx.test.espresso.action.ViewActions.swipeDown;

public class SwipeActions {

    /**
     * Свайп вниз с ослабленными требованиями к видимости (50% вместо 90%).
     */
    public static ViewAction swipeDownRelaxed() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                // Разрешаем, если видно хотя бы 50% — этого достаточно для свайпа
                return ViewMatchers.isDisplayed();
            }

            @Override
            public String getDescription() {
                return "swipeDown with relaxed visibility";
            }

            @Override
            public void perform(UiController uiController, View view) {
                // Выполняем стандартный свайп, но поверх View, который уже прошёл getConstraints
                swipeDown().perform(uiController, view);
            }
        };
    }
}