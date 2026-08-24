package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;

import android.view.View;
import android.widget.Checkable;

import org.hamcrest.Matcher;

import ru.edu.qamid.R;

public class FilterCPNewsWindow extends FilterNewsWindow {
    private static final int ID_ACTIVE_CHECKBOX = R.id.filter_news_active_check_box;
    private static final int ID_INACTIVE_CHECKBOX = R.id.filter_news_inactive_check_box;

    public FilterCPNewsWindow clickActiveCheckbox() {
        reportAllureStep("Нажимаем чекбокс «Активна»", () -> {

            Matcher<View> checkbox = withId(ID_ACTIVE_CHECKBOX);
            onView(checkbox).perform(click());

        });
        return this;
    }

    public FilterCPNewsWindow clickInactiveCheckbox() {
        reportAllureStep("Нажимаем чекбокс «Не активна»", () -> {

            Matcher<View> checkbox = withId(ID_INACTIVE_CHECKBOX);
            onView(checkbox).perform(click());

        });
        return this;
    }

    /**
     * Гарантирует, что чекбокс «Активна» находится в нужном состоянии.
     * Если состояние уже верное — ничего не делает. Если нет — кликает 1 раз.
     */
    public FilterCPNewsWindow ensureActiveCheckbox(boolean shouldBeChecked) {
        reportAllureStep("Устанавливаем чекбокс «Активна» в состояние " + shouldBeChecked, () -> {

            Matcher<View> checkbox = withId(ID_ACTIVE_CHECKBOX);
            boolean currentState = getCurrentCheckboxState(checkbox);

            if (currentState != shouldBeChecked) {
                onView(checkbox).perform(click());

                Matcher<View> expectedMatcher = shouldBeChecked
                        ? isChecked()
                        : not(isChecked());

                onView(checkbox).check(matches(expectedMatcher));
            }

        });
        return this;
    }

    /**
     * Гарантирует, что чекбокс «Не активна» находится в нужном состоянии.
     * Если состояние уже верное — ничего не делает. Если нет — кликает 1 раз.
     */
    public FilterCPNewsWindow ensureInactiveCheckbox(boolean shouldBeChecked) {
        reportAllureStep("Устанавливаем чекбокс «Не активна» в состояние " + shouldBeChecked, () -> {

            Matcher<View> checkbox = withId(ID_INACTIVE_CHECKBOX);
            boolean currentState = getCurrentCheckboxState(checkbox);

            if (currentState != shouldBeChecked) {
                onView(checkbox).perform(click());

                Matcher<View> expectedMatcher = shouldBeChecked
                        ? isChecked()
                        : not(isChecked());

                onView(checkbox).check(matches(expectedMatcher));
            }

        });
        return this;
    }

    /**
     * Вспомогательный метод для получения состояния чекбокса.
     */
    private boolean getCurrentCheckboxState(Matcher<View> matcher) {
        final boolean[] result = new boolean[1];
        onView(matcher).check((view, noViewException) -> {
            if (view instanceof Checkable) {
                result[0] = ((Checkable) view).isChecked();
            } else {
                throw new RuntimeException("View is not Checkable: " + view.getClass().getName());
            }
        });
        return result[0];
    }

    /**
     * Проверяет состояние чекбокса "Активна".
     *
     * @param checked true — ожидаем, что чекбокс отмечен; false — ожидаем, что снят.
     */
    public FilterCPNewsWindow assertActiveCheckboxChecked(boolean checked) {
        reportAllureStep("Проверяем чекбокс «Активна» (Отмечен: " + checked + ")", () -> {

            Matcher<View> matcher = withId(ID_ACTIVE_CHECKBOX);

            if (checked) {
                onView(matcher).check(matches(isChecked()));
            } else {
                onView(matcher).check(matches(not(isChecked())));
            }

        });
        return this;
    }

    /**
     * Проверяет состояние чекбокса "Не активна".
     *
     * @param checked true — ожидаем, что чекбокс отмечен; false — ожидаем, что снят.
     */
    public FilterCPNewsWindow assertInactiveCheckboxChecked(boolean checked) {
        reportAllureStep("Проверяем чекбокс «Не активна» (Отмечен: " + checked + ")", () -> {

            Matcher<View> matcher = withId(ID_INACTIVE_CHECKBOX);

            if (checked) {
                onView(matcher).check(matches(isChecked()));
            } else {
                onView(matcher).check(matches(not(isChecked())));
            }

        });
        return this;
    }
}
