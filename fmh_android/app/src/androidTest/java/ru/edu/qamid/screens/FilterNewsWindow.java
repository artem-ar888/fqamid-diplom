package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;

import ru.edu.qamid.R;
import ru.edu.qamid.utils.helpers.AutoCompleteHelper;

public class FilterNewsWindow {
    private static final int ID_FILTER_TITLE = R.id.filter_news_title_text_view;
    private static final int ID_NEWS_CATEGORY = R.id.filter_news_category_auto_complete;
    private static final int ID_DATE_START = R.id.filter_news_date_start_edit_text;
    private static final int ID_DATE_END = R.id.filter_news_date_end_edit_text;
    private static final int ID_APPLY_BTN = R.id.filter_news_apply_button;
    private static final int ID_CANCEL_BTN = R.id.filter_news_cancel_button;

    public FilterNewsWindow assertFilterNewsIsDisplayed() {
        reportAllureStep("Проверяем, что окно «Фильтровать новости» отображается", () -> {

            onView(withId(ID_FILTER_TITLE))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public FilterNewsWindow selectCategory(String categoryName) {
        reportAllureStep("Выбираем категорию новости «" + categoryName + "» в фильтре", () -> {

            AutoCompleteHelper.selectCategory(
                    withId(ID_NEWS_CATEGORY),
                    categoryName
            );

        });
        return this;
    }

    // Если нужно передать разные значения для ввода и выбора категории
    public FilterNewsWindow selectCategoryAdvanced(String inputText, String expectedListItemText) {
        reportAllureStep("Выбираем категорию новости: вводим «" + inputText +
                "», подтверждаем элемент «" + expectedListItemText + "»", () -> {

            AutoCompleteHelper.selectCategory(
                    withId(ID_NEWS_CATEGORY),
                    inputText,
                    expectedListItemText
            );

        });
        return this;
    }

    // Заполнение даты начала
    public FilterNewsWindow setStartDate(String dateString) {
        reportAllureStep("Устанавливаем дату начала фильтрации: «" + dateString + "»", () -> {

            onView(withId(ID_DATE_START))
                    .check(matches(isDisplayed()))
                    .perform(replaceText(dateString));

        });
        return this;
    }

    // Заполнение даты конца
    public FilterNewsWindow setEndDate(String dateString) {
        reportAllureStep("Устанавливаем дату окончания фильтрации: «" + dateString + "»", () -> {

            onView(withId(ID_DATE_END))
                    .check(matches(isDisplayed()))
                    .perform(replaceText(dateString));

        });
        return this;
    }

    // Применение фильтра
    public FilterNewsWindow apply() {
        reportAllureStep("Применяем фильтр новостей", () -> {

            onView(withId(ID_APPLY_BTN))
                    .check(matches(isDisplayed()))
                    .perform(click());

        });
        return this;
    }

    public void cancel() {
        reportAllureStep("Отменяем фильтрацию (нажимаем кнопку «Отмена»)", () -> {

            onView(withId(ID_CANCEL_BTN))
                    .check(matches(isDisplayed()))
                    .perform(click());

        });
    }
}

