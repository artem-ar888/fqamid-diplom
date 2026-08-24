package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;
import static ru.edu.qamid.utils.helpers.ResourceHelper.getString;

import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Matcher;

import ru.edu.qamid.R;
import ru.edu.qamid.utils.helpers.AutoCompleteHelper;

public class CreateEditNewsForm {
    private static final int ID_CE_APP_BAR = R.id.create_edit_news_app_bar;
    private static final int ID_CE_APP_BAR_TITLE = R.id.custom_app_bar_title_text_view;
    private static final String RES_CREATING_NEWS_STR = getString(R.string.creating);
    private static final String RES_EDITING_NEWS_STR = getString(R.string.editing);
    private static final int ID_NEWS_CATEGORY = R.id.news_category_auto_complete;
    private static final int ID_NEWS_TITLE = R.id.news_title_edit_text;
    private static final int ID_NEWS_PUBLISH_DATE = R.id.news_publish_date_edit_text;
    private static final int ID_NEWS_PUBLISH_TIME = R.id.news_publish_time_edit_text;
    private static final int ID_NEWS_DESCRIPTION = R.id.news_description_edit_text;
    private static final int ID_NEWS_ACTIVE_SWITCH = R.id.news_active_switch;
    private static final int ID_SAVE_BTN = R.id.news_save_button;
    private static final int ID_CANCEL_BTN = R.id.news_cancel_button;
    private static final String RES_CANCELLATION_STR = getString(R.string.cancellation);
    private static final int ID_ANDROID_OK_BTN = android.R.id.button1;
    private static final int ID_ANDROID_CANCEL_BTN = android.R.id.button2;
    private static final int ID_CATEGORY_LAYOUT = R.id.news_category_text_input_layout;
    private static final int ID_TITLE_LAYOUT = R.id.news_title_text_input_layout;
    private static final int ID_PUBLISH_DATE_LAYOUT = R.id.news_publish_date_text_input_layout;
    private static final int ID_PUBLISH_TIME_LAYOUT = R.id.news_publish_time_text_input_layout;
    private static final int ID_DESC_LAYOUT = R.id.news_description_text_input_layout;
    private static final String RES_EMPTY_FIELDS_STR = getString(R.string.empty_fields);


    public CreateEditNewsForm assertCreateEditNewsFormIsDisplayed() {
        reportAllureStep("Проверяем, что форма создания/редактирования новости отображается", () -> {

            onView(withId(ID_CE_APP_BAR))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public CreateEditNewsForm assertIsCreatingForm() {
        reportAllureStep("Проверяем, что открыта форма «Создание новости»", () -> {

            onView(withId(ID_CE_APP_BAR_TITLE))
                    .check(matches(allOf(isDisplayed(), withText(RES_CREATING_NEWS_STR))));

        });
        return this;
    }

    public CreateEditNewsForm assertIsEditingForm() {
        reportAllureStep("Проверяем, что открыта форма «Редактирование новости»", () -> {

            onView(withId(ID_CE_APP_BAR_TITLE))
                    .check(matches(allOf(isDisplayed(), withText(RES_EDITING_NEWS_STR))));

        });
        return this;
    }

    public CreateEditNewsForm selectCategory(String categoryName) {
        reportAllureStep("Выбираем категорию новости «" + categoryName + "»", () -> {

            onView(withId(ID_NEWS_CATEGORY))
                    .perform(scrollTo());
            AutoCompleteHelper.selectCategory(
                    withId(ID_NEWS_CATEGORY),
                    categoryName
            );

        });
        return this;
    }

    // Если нужно передать разные значения для ввода и выбора категории
    public CreateEditNewsForm selectCategoryAdvanced(String inputText, String expectedListItemText) {
        reportAllureStep("Выбираем категорию новости: вводим «" + inputText +
                "», подтверждаем элемент «" + expectedListItemText + "»", () -> {

            onView(withId(ID_NEWS_CATEGORY))
                    .perform(scrollTo());
            AutoCompleteHelper.selectCategory(
                    withId(ID_NEWS_CATEGORY),
                    inputText,
                    expectedListItemText
            );

        });
        return this;
    }

    public CreateEditNewsForm enterNewsTitle(String titleString) {
        reportAllureStep("Вводим заголовок новости: «" + titleString + "»", () -> {

            onView(withId(ID_NEWS_TITLE))
                    .perform(scrollTo(), replaceText(titleString));

        });
        return this;
    }

    public CreateEditNewsForm enterNewsPublishDate(String dateString) {
        reportAllureStep("Вводим дату публикации: «" + dateString + "»", () -> {

            onView(withId(ID_NEWS_PUBLISH_DATE))
                    .perform(scrollTo(), replaceText(dateString));

        });
        return this;
    }

    public CreateEditNewsForm enterNewsPublishTime(String timeString) {
        reportAllureStep("Вводим время публикации: «" + timeString + "»", () -> {

            onView(withId(ID_NEWS_PUBLISH_TIME))
                    .perform(scrollTo(), replaceText(timeString));

        });
        return this;
    }

    public CreateEditNewsForm enterNewsDescription(String descriptionString) {
        reportAllureStep("Вводим описание новости: «" + descriptionString + "»", () -> {

            onView(withId(ID_NEWS_DESCRIPTION))
                    .perform(scrollTo(), replaceText(descriptionString));

        });
        return this;
    }

    /**
     * Гарантирует, что переключатель news_active_switch находится в нужном состоянии.
     * Если состояние уже верное — ничего не делает. Если нет — кликает и ждёт обновления.
     */
    public CreateEditNewsForm ensureSwitchActiveState(boolean shouldBeOn) {
        reportAllureStep("Устанавливаем переключатель «Активна» в состояние " + shouldBeOn, () -> {

            Matcher<View> matcher = withId(ID_NEWS_ACTIVE_SWITCH);

            onView(matcher).perform(scrollTo());

            boolean currentState = getSwitchCurrentState(matcher);

            if (currentState != shouldBeOn) {
                // Кликаем только если состояние не совпадает
                onView(matcher).perform(scrollTo(), click());

                // Ждём, пока UI реально обновится до нужного состояния
                Matcher<View> expectedMatcher = shouldBeOn
                        ? isChecked()
                        : not(isChecked());

                onView(matcher)
                        .perform(scrollTo())
                        .check(matches(expectedMatcher));
            }

        });
        return this;
    }

    /**
     * Вспомогательный метод для получения текущего состояния свитча.
     * Использует массив-обманку, чтобы вытащить boolean из лямбды Espresso.
     */
    private boolean getSwitchCurrentState(Matcher<View> matcher) {
        final boolean[] result = new boolean[1];

        onView(matcher).check((view, noViewException) -> {
            if (noViewException != null) {
                throw new RuntimeException("Switch not found", noViewException);
            }
            // SwitchMaterial наследуется от CompoundButton, который реализует Checkable
            if (!(view instanceof android.widget.CompoundButton)) {
                throw new RuntimeException(
                        "Expected CompoundButton for switch, but got: " + view.getClass().getName()
                );
            }
            result[0] = ((android.widget.CompoundButton) view).isChecked();
        });

        return result[0];
    }

    /**
     * Универсальная проверка состояния свитча.
     *
     * @param shouldBeEnabled true -> проверяем, что можно нажимать (enabled)
     *                        false -> проверяем, что нельзя (disabled)
     */
    public CreateEditNewsForm assertSwitchEnabledState(boolean shouldBeEnabled) {
        reportAllureStep("Проверяем, что переключатель «Активна» можно нажать " +
                "(Ожидаем: " + shouldBeEnabled + ")", () -> {

            Matcher<View> matcher = withId(ID_NEWS_ACTIVE_SWITCH);
            Matcher<View> condition = shouldBeEnabled ? isEnabled() : not(isEnabled());

            onView(matcher)
                    .perform(scrollTo())
                    .check(matches(allOf(isDisplayed(), condition)));

        });
        return this;
    }

    /**
     * Универсальная проверка состояния свитча.
     *
     * @param shouldBeChecked true -> проверяем, что включён
     *                        false -> проверяем, что выключен
     */
    public CreateEditNewsForm assertSwitchCheckedState(boolean shouldBeChecked) {
        reportAllureStep("Проверяем, что переключатель «Активна» находится во " +
                "включённом состоянии (Ожидаем: " + shouldBeChecked + ")", () -> {

            Matcher<View> matcher = withId(ID_NEWS_ACTIVE_SWITCH);
            Matcher<View> condition = shouldBeChecked ? isChecked() : not(isChecked());

            onView(matcher)
                    .perform(scrollTo())
                    .check(matches(allOf(isDisplayed(), condition)));

        });
        return this;
    }

    public CreateEditNewsForm save() {
        reportAllureStep("Нажимаем кнопку «Сохранить»", () -> {

            onView(withId(ID_SAVE_BTN))
                    .perform(scrollTo(), click());

        });
        return this;
    }

    public CreateEditNewsForm cancel() {
        clickCancelButton();
        verifyCancelConfirmationDialog();
        confirmCancel();
        return this;
    }

    public CreateEditNewsForm clickCancelButton() {
        reportAllureStep("Нажимаем кнопку «Отмена» на форме", () -> {

            onView(withId(ID_CANCEL_BTN))
                    .perform(scrollTo(), click());

        });
        return this;
    }

    /**
     * Проверяет, что диалог отмены открыт и содержит ожидаемое сообщение.
     */
    public CreateEditNewsForm verifyCancelConfirmationDialog() {
        reportAllureStep("Проверяем, что открыто диалоговое окно подтверждения отмены", () -> {

            // Ищем текст сообщения ТОЛЬКО внутри диалоговых окон
            onView(withText(RES_CANCELLATION_STR))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    /**
     * Нажимает кнопку "OK" (подтвердить отмену изменений).
     */
    public CreateEditNewsForm confirmCancel() {
        reportAllureStep("Подтверждаем отмену изменений, нажимая кнопку «OK» в диалоге", () -> {

            onView(withId(ID_ANDROID_OK_BTN))
                    .inRoot(isDialog())
                    .perform(click());

        });
        return this;
    }

    /**
     * Нажимает кнопку "CANCEL" (отмена, возврат к форме).
     */
    public CreateEditNewsForm cancelCancel() {
        reportAllureStep("Отменяем закрытие формы, нажимая кнопку «Отмена» в диалоговом окне", () -> {

            onView(withId(ID_ANDROID_CANCEL_BTN))
                    .inRoot(isDialog())
                    .perform(click());

        });
        return this;
    }

    private void assertErrorIconVisibleForLayout(int layoutId) {
        onView(withId(layoutId))
                .perform(scrollTo())
                .check(matches(hasDescendant(allOf(
                        isAssignableFrom(ImageView.class),
                        isDisplayed()
                ))));
    }

    public CreateEditNewsForm assertCategoryHasErrorIcon() {
        reportAllureStep("Проверяем, что в поле «Категория» отображается иконка ошибки", () -> {

            assertErrorIconVisibleForLayout(ID_CATEGORY_LAYOUT);

        });
        return this;
    }

    public CreateEditNewsForm assertTitleHasErrorIcon() {
        reportAllureStep("Проверяем, что в поле «Заголовок» отображается иконка ошибки", () -> {

            assertErrorIconVisibleForLayout(ID_TITLE_LAYOUT);

        });
        return this;
    }

    public CreateEditNewsForm assertPublishDateHasErrorIcon() {
        reportAllureStep("Проверяем, что в поле «Дата публикации» отображается иконка ошибки", () -> {

            assertErrorIconVisibleForLayout(ID_PUBLISH_DATE_LAYOUT);

        });
        return this;
    }

    public CreateEditNewsForm assertPublishTimeHasErrorIcon() {
        reportAllureStep("Проверяем, что в поле «Время публикации» отображается иконка ошибки", () -> {

            assertErrorIconVisibleForLayout(ID_PUBLISH_TIME_LAYOUT);

        });
        return this;
    }

    public CreateEditNewsForm assertDescriptionHasErrorIcon() {
        reportAllureStep("Проверяем, что в поле «Описание» отображается иконка ошибки", () -> {

            assertErrorIconVisibleForLayout(ID_DESC_LAYOUT);

        });
        return this;
    }

}



