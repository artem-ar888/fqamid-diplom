package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;
import static ru.edu.qamid.utils.helpers.ConvertHelper.checkAndConvertHumanPositionToZeroBased;
import static ru.edu.qamid.utils.helpers.ListActions.assertCardDoesNotExist;
import static ru.edu.qamid.utils.helpers.ListActions.clickButtonInCard;
import static ru.edu.qamid.utils.helpers.ListActions.scrollToAndAssertCardByTwoTexts;
import static ru.edu.qamid.utils.helpers.RecyclerViewHelper.openItemAtPositionWithConvertPosition;

import androidx.test.espresso.action.ViewActions;

import ru.edu.qamid.R;
import ru.edu.qamid.utils.data.ItemType;
import ru.edu.qamid.utils.data.NewsItem;
import ru.edu.qamid.utils.helpers.RecyclerViewHelper;

public class NewsScreen extends AppBar {
    public NewsScreen() {
    }

    private static final int ID_APP_BAR = R.id.news_list_app_bar;
    private static final int ID_LIST = R.id.news_list_recycler_view;
    private static final int ID_NEWS_EL_TITLE = R.id.news_item_title_text_view;
    private static final int ID_NEWS_EL_DESC = R.id.news_item_description_text_view;
    private static final int ID_NEWS_EL_PUB_DATE = R.id.news_item_date_text_view;
    private static final int ID_NEWS_EL_CATEGORY = R.id.news_item_category_text_view;
    private static final int ID_NEWS_EL_EXPAND_BTN = R.id.news_item_expand_image_view;
    private static final int ID_MATERIAL_CARD = R.id.news_item_material_card_view;
    private static final int ID_NEWS_CP_BTN = R.id.news_edit_button;
    private static final int ID_SWIPE_REFRESH = R.id.news_list_swipe_refresh;
    private static final int ID_SORT_NEWS_BTN = R.id.news_sort_button;
    private static final int ID_FILTER_NEWS_BTN = R.id.news_filter_button;

    public NewsScreen assertNewsScreenIsDisplayed() {
        reportAllureStep("Проверяем, что экран «Новости» отображается", () -> {

            onView(withId(ID_APP_BAR))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public NewsScreen clickSortNewsButton() {
        reportAllureStep("Нажимаем кнопку сортировки новостей", () -> {

            onView(withId(ID_SORT_NEWS_BTN))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.click());

        });
        return this;
    }

    public NewsScreen clickFilterNewsButton() {
        reportAllureStep("Нажимаем кнопку фильтрации новостей", () -> {

            onView(withId(ID_FILTER_NEWS_BTN))
                    .check(matches(isDisplayed()))
                    .perform(ViewActions.click());

        });
        return this;
    }

    public void clickNewsEditButton() {
        reportAllureStep("Нажимаем кнопку перехода к экрану «Панель управления»", () -> {

            onView(withId(ID_NEWS_CP_BTN))
                    .check(matches(isDisplayed()))
                    .perform(click());

        });
    }

    public NewsScreen assertNewsListIsDisplayed() {
        reportAllureStep("Проверяем, что список новостей (RecyclerView) отображается", () -> {

            onView(withId(ID_LIST))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public NewsScreen performSwipeToRefresh() {
        reportAllureStep("Выполняем свайп для обновления списка новостей", () -> {

            performSwipeOnRefreshLayout(ID_SWIPE_REFRESH);

        });
        return this;
    }

    /**
     * Получает данные из карточки. Работает для любых позиций.
     */
    public NewsItem getNewsAtPosition(int humanPosition) {
        int index = checkAndConvertHumanPositionToZeroBased(humanPosition);
        return RecyclerViewHelper.getItemAtPosition(
                ItemType.NEWS,
                ID_LIST,
                index,
                ID_NEWS_EL_TITLE,
                ID_NEWS_EL_DESC,
                ID_NEWS_EL_PUB_DATE,
                ID_NEWS_EL_CATEGORY
        );
    }

    public void openCardAtPosition(int humanPosition) {
        reportAllureStep("Открываем карточку новости на позиции " + humanPosition, () -> {

            openItemAtPositionWithConvertPosition(ID_LIST, humanPosition, ID_MATERIAL_CARD);

        });
    }

    public void closeCardAtPosition(int humanPosition) {
        reportAllureStep("Закрываем карточку новости на позиции " + humanPosition +
                " (повторным нажатием)", () -> {

            openCardAtPosition(humanPosition);

        });
    }

    public NewsScreen assertNewsDoesNotExist(String titleText, String descriptionText) {
        reportAllureStep("Проверяем, что новость с заголовком «" + titleText +
                "» и описанием «" + descriptionText + "» отсутствует в списке", () -> {

            assertCardDoesNotExist(
                    ID_LIST,
                    ID_NEWS_EL_TITLE, titleText,
                    ID_NEWS_EL_DESC, descriptionText);

        });
        return this;
    }

    public NewsScreen assertCardByTwoTexts(String titleText, String descriptionText) {
        reportAllureStep("Проверяем наличие карточки новости: заголовок «" + titleText +
                "», описание «" + descriptionText + "»", () -> {

            scrollToAndAssertCardByTwoTexts(
                    ID_LIST,
                    ID_NEWS_EL_TITLE, titleText,
                    ID_NEWS_EL_DESC, descriptionText);

        });
        return this;
    }

    public NewsScreen openCardByTwoTexts(String titleText, String descriptionText) {
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

    public NewsScreen assertNewsTitleIsDisplayed(String titleText) {
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

    public NewsScreen assertNewsDescriptionIsDisplayed(String descriptionText) {
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
