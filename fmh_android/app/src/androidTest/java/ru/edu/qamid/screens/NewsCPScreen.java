package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;
import static ru.edu.qamid.utils.helpers.ConvertHelper.checkAndConvertHumanPositionToZeroBased;
import static ru.edu.qamid.utils.helpers.ListActions.assertCardDoesNotExist;
import static ru.edu.qamid.utils.helpers.ListActions.clickButtonInCard;
import static ru.edu.qamid.utils.helpers.ListActions.clickInCardAtPosition;
import static ru.edu.qamid.utils.helpers.ListActions.scrollToAndAssertCardByTwoTexts;
import static ru.edu.qamid.utils.helpers.RecyclerViewHelper.clickViewAtPositionSafe;
import static ru.edu.qamid.utils.helpers.RecyclerViewHelper.findPositionByText;
import static ru.edu.qamid.utils.helpers.RecyclerViewHelper.openItemAtPositionWithConvertPosition;
import static ru.edu.qamid.utils.helpers.ResourceHelper.getString;

import androidx.test.espresso.action.ViewActions;

import ru.edu.qamid.R;
import ru.edu.qamid.utils.data.AdminNewsItem;
import ru.edu.qamid.utils.data.ItemType;
import ru.edu.qamid.utils.helpers.RecyclerViewHelper;

public class NewsCPScreen extends AppBar {

    public NewsCPScreen() {
    }

    private static final int ID_APP_BAR = R.id.news_control_panel_app_bar;
    private static final int ID_LIST = R.id.news_list_recycler_view;
    private static final int ID_NEWS_EL_TITLE = R.id.news_item_title_text_view;
    private static final int ID_NEWS_EL_DESC = R.id.news_item_description_text_view;
    private static final int ID_NEWS_EL_PUB_DATE = R.id.news_item_publication_date_text_view;
    private static final int ID_NEWS_EL_CREATE_DATE = R.id.news_item_create_date_text_view;
    private static final int ID_NEWS_EL_AUTHOR = R.id.news_item_author_name_text_view;
    private static final int ID_NEWS_EL_PUBLISHED = R.id.news_item_published_text_view;
    private static final int ID_NEWS_EL_CATEGORY_ICON = R.id.category_icon_image_view;
    private static final int ID_NEWS_EL_DELETE_BTN = R.id.news_item_delete_image_view;
    private static final int ID_NEWS_EL_EDIT_BTN = R.id.news_item_edit_image_view;
    private static final int ID_NEWS_EL_EXPAND_BTN = R.id.news_item_expand_image_view;
    private static final int ID_MATERIAL_CARD = R.id.news_item_material_card_view;
    private static final int ID_SWIPE_REFRESH = R.id.news_control_panel_swipe_to_refresh;
    private static final String RES_IRREVOCABLE_DELETION_STR = getString(R.string.irrevocable_deletion);
    private static final int ID_SORT_NEWS_BTN = R.id.news_sort_button;
    private static final int ID_FILTER_NEWS_BTN = R.id.news_filter_button;
    private static final int ID_CREATE_NEWS_BTN = R.id.add_news_image_view;
    private static final int ID_ANDROID_OK_BTN = android.R.id.button1;
    private static final int ID_ANDROID_CANCEL_BTN = android.R.id.button2;

    public NewsCPScreen assertNewsCPScreenIsDisplayed() {
        reportAllureStep("Проверяем, что экран «Панель управления» отображается", () -> {

            onView(withId(ID_APP_BAR))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public NewsCPScreen clickSortNewsButton() {
        reportAllureStep("Нажимаем кнопку сортировки новостей", () -> {

            onView(withId(ID_SORT_NEWS_BTN))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.click());

        });
        return this;
    }

    public NewsCPScreen clickFilterNewsButton() {
        reportAllureStep("Нажимаем кнопку фильтрации новостей", () -> {

            onView(withId(ID_FILTER_NEWS_BTN))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.click());

        });
        return this;
    }

    public NewsCPScreen clickCreateNewsButton() {
        reportAllureStep("Нажимаем кнопку создания новости", () -> {

            onView(withId(ID_CREATE_NEWS_BTN))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.click());

        });
        return this;
    }

    public NewsCPScreen assertNewsListIsDisplayed() {
        reportAllureStep("Проверяем, что список новостей (RecyclerView) отображается", () -> {

            onView(withId(ID_LIST))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public NewsCPScreen performSwipeToRefresh() {
        reportAllureStep("Выполняем свайп для обновления списка новостей на панели управления", () -> {

            performSwipeOnRefreshLayout(ID_SWIPE_REFRESH);

        });
        return this;
    }

    public NewsCPScreen checkNewsPublishedTextIsDisplayed() {
        reportAllureStep("Проверяем, что в карточках новостей отображается статус " +
                "«Активна»/«Не активна»", () -> {

            onView(withId(ID_LIST))
                    .check(matches(hasDescendant(allOf(
                            withId(ID_NEWS_EL_PUBLISHED),
                            isDisplayed()
                    ))));

        });
        return this;
    }


    private void clickNewsElementAtPosition(int humanPosition, int viewId) {
        int index = checkAndConvertHumanPositionToZeroBased(humanPosition);
        clickViewAtPositionSafe(ID_LIST, index, viewId);
    }

//    public NewsCPScreen clickNewsDeleteButtonAtPositionOldMethod(int humanPosition) {
//    Allure.step("Нажимаем кнопку удаления новости на позиции " + humanPosition);
//        clickNewsElementAtPosition(humanPosition, ID_NEWS_EL_DELETE_BTN);
//        return this;
//    }

    public NewsCPScreen clickNewsDeleteButtonAtPosition(int humanPosition) {
        reportAllureStep("Нажимаем кнопку удаления у новости на позиции " +
                humanPosition, () -> {

            clickInCardAtPosition(ID_LIST, humanPosition, ID_NEWS_EL_DELETE_BTN);

        });
        return this;
    }

    public NewsCPScreen deleteCardByTitle(String titleText) {
        reportAllureStep("Удаляем карточку новости с заголовком «" + titleText +
                "»" , () -> {

            int humanPosition = findPositionByTitle(titleText) + 1;
            deleteCardAtPosition(humanPosition);

        });
        return this;
    }

    public NewsCPScreen deleteCardAtPosition(int humanPosition) {
        clickNewsDeleteButtonAtPosition(humanPosition);
        deleteCard();
        return this;
    }

    private void deleteCard() {
        verifyDeleteConfirmationDialog();
        confirmDelete();
    }

    /**
     * Проверяет, что диалог удаления открыт и содержит ожидаемое сообщение.
     */
    public NewsCPScreen verifyDeleteConfirmationDialog() {
        reportAllureStep("Проверяем, что открыто диалоговое окно подтверждения удаления", () -> {

            // Ищем текст сообщения ТОЛЬКО внутри диалоговых окон
            onView(withText(RES_IRREVOCABLE_DELETION_STR))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    /**
     * Нажимает кнопку "OK" (подтверждение удаления).
     */
    public NewsCPScreen confirmDelete() {
        reportAllureStep("Подтверждаем удаление новости, нажимая кнопку «OK» в диалоговом окне", () -> {

            onView(withId(ID_ANDROID_OK_BTN))
                    .inRoot(isDialog())
                    .perform(click());

        });
        return this;
    }

    /**
     * Нажимает кнопку "CANCEL" (отмена).
     */
    public NewsCPScreen cancelDelete() {
        reportAllureStep("Отменяем удаление новости, нажимая кнопку «Отмена» в диалоговом окне", () -> {

            onView(withId(ID_ANDROID_CANCEL_BTN))
                    .inRoot(isDialog())
                    .perform(click());

        });
        return this;
    }

    public NewsCPScreen assertNewsDoesNotExist(String titleText, String descriptionText) {
        reportAllureStep("Проверяем, что новость с заголовком «" + titleText +
                "» и описанием «" + descriptionText + "» отсутствует в списке", () -> {

            assertCardDoesNotExist(
                    ID_LIST,
                    ID_NEWS_EL_TITLE, titleText,
                    ID_NEWS_EL_DESC, descriptionText);

        });
        return this;
    }

    public NewsCPScreen assertCardByTwoTexts(String titleText, String descriptionText) {
        reportAllureStep("Проверяем наличие карточки новости: заголовок «" + titleText +
                "», описание «" + descriptionText + "»", () -> {

            scrollToAndAssertCardByTwoTexts(
                    ID_LIST,
                    ID_NEWS_EL_TITLE, titleText,
                    ID_NEWS_EL_DESC, descriptionText);

        });
        return this;
    }

    public void clickNewsEditButtonAtPosition(int humanPosition) {
        reportAllureStep("Нажимаем кнопку редактирования новости на позиции "
                + humanPosition, () -> {

            clickInCardAtPosition(ID_LIST, humanPosition, ID_NEWS_EL_EDIT_BTN);

        });
    }

    /**
     * Получает данные из карточки. Работает для любых позиций.
     */
    public AdminNewsItem getAdminNewsAtPosition(int humanPosition) {
        int index = checkAndConvertHumanPositionToZeroBased(humanPosition);
        return RecyclerViewHelper.getItemAtPosition(
                ItemType.ADMIN_NEWS,
                ID_LIST,
                index,
                ID_NEWS_EL_TITLE,
                ID_NEWS_EL_DESC,
                ID_NEWS_EL_PUB_DATE,
                ID_NEWS_EL_CATEGORY_ICON,
                ID_NEWS_EL_CREATE_DATE,
                ID_NEWS_EL_AUTHOR,
                ID_NEWS_EL_PUBLISHED
        );
    }

    public NewsCPScreen openCardAtPosition(int humanPosition) {
        reportAllureStep("Открываем карточку новости на позиции " + humanPosition, () -> {

            openItemAtPositionWithConvertPosition(ID_LIST, humanPosition, ID_MATERIAL_CARD);

        });
        return this;
    }

    public NewsCPScreen closeCardAtPosition(int humanPosition) {
        reportAllureStep("Закрываем карточку новости на позиции " + humanPosition +
                " (повторным нажатием)", () -> {

            openCardAtPosition(humanPosition);

        });
        return this;
    }

    public NewsCPScreen openCardByTwoTexts(String titleText, String descriptionText) {
        reportAllureStep("Открываем карточку новости по заголовку «" + titleText +
                "» и описанию «" + descriptionText + "»", () -> {

            clickButtonInCard(
                    ID_LIST,
                    ID_MATERIAL_CARD,
                    ID_NEWS_EL_TITLE, titleText,
                    ID_NEWS_EL_DESC, descriptionText
            );

        });
        return this;
    }


    public int findPositionByTitle(String titleText) {
        return findPositionByText(ID_LIST, ID_NEWS_EL_TITLE, titleText);
    }

    public NewsCPScreen assertNewsTitleIsDisplayed(String titleText) {
        reportAllureStep("Проверяем, что заголовок новости «" + titleText +
                "» отображается на экране", () -> {

            onView(allOf(
                    withId(ID_NEWS_EL_TITLE),
                    withText(titleText)
            ))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public NewsCPScreen assertNewsDescriptionIsDisplayed(String descriptionText) {
        reportAllureStep("Проверяем, что описание новости «" + descriptionText +
                "» отображается на экране", () -> {

            onView(allOf(
                    withId(ID_NEWS_EL_DESC),
                    withText(descriptionText)
            ))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

}
