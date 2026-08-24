package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;
import static ru.edu.qamid.utils.helpers.ListActions.assertCardDoesNotExist;
import static ru.edu.qamid.utils.helpers.ListActions.scrollToAndAssertCardByTwoTexts;

import ru.edu.qamid.R;

public class MainScreen extends AppBar {

    public MainScreen() {
    }

    private static final int ID_APP_BAR = R.id.main_app_bar;
    private static final int ID_NEWS_CONTAINER = R.id.main_news_list_container;
    private static final int ID_ALL_NEWS_BUTTON = R.id.all_news_text_view;
    private static final int ID_SWIPE_REFRESH = R.id.main_swipe_refresh;
    private static final int ID_NEWS_LIST = R.id.news_list_recycler_view;
    private static final int ID_NEWS_EL_TITLE = R.id.news_item_title_text_view;
    private static final int ID_NEWS_EL_DESC = R.id.news_item_description_text_view;
    private static final int ID_NEWS_EL_PUB_DATE = R.id.news_item_date_text_view;

    public MainScreen assertMainScreenIsDisplayed() {
        reportAllureStep("Проверяем, что экран «Главная» отображается", () -> {

            onView(withId(ID_APP_BAR))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public MainScreen assertNewsContainerIsDisplayed() {
        reportAllureStep("Проверяем, что контейнер списка новостей отображается", () -> {

            onView(withId(ID_NEWS_CONTAINER))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public void clickAllNewsButton() {
        reportAllureStep("Нажимаем кнопку «Все новости»", () -> {

            onView(withId(ID_ALL_NEWS_BUTTON))
                    .check(matches(isDisplayed()))
                    .perform(click());

        });
    }

    public MainScreen performSwipeToRefresh() {
        reportAllureStep("Выполняем свайп для обновления списка новостей", () -> {

            performSwipeOnRefreshLayout(ID_SWIPE_REFRESH);

        });
        return this;
    }

    public MainScreen assertNewsListIsDisplayed() {
        reportAllureStep("Проверяем, что RecyclerView со списком новостей отображается", () -> {

            onView(withId(ID_NEWS_LIST))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    public MainScreen assertNewsDoesNotExist(String titleText, String descriptionText) {
        reportAllureStep("Проверяем, что новость с заголовком «" + titleText +
                "» и описанием «" + descriptionText + "» отсутствует в списке", () -> {

            assertCardDoesNotExist(
                    ID_NEWS_LIST,
                    ID_NEWS_EL_TITLE, titleText,
                    ID_NEWS_EL_DESC, descriptionText);

        });
        return this;
    }

    public MainScreen assertCardByTwoTexts(String titleText, String descriptionText) {
        reportAllureStep("Проверяем наличие карточки новости: заголовок «" + titleText +
                "», описание «" + descriptionText + "»", () -> {

            scrollToAndAssertCardByTwoTexts(
                    ID_NEWS_LIST,
                    ID_NEWS_EL_TITLE, titleText,
                    ID_NEWS_EL_DESC, descriptionText);

        });
        return this;
    }

}
