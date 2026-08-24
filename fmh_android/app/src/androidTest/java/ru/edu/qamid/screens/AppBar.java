package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;
import static ru.edu.qamid.utils.helpers.ResourceHelper.getString;
import static ru.edu.qamid.utils.matchers.MenuMatchers.withMenuItemEnabled;
import static ru.edu.qamid.utils.matchers.MenuMatchers.withMenuItemId;
import static ru.edu.qamid.utils.matchers.MenuMatchers.withMenuItemTitle;

import android.view.MenuItem;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;

import ru.edu.qamid.R;
import ru.edu.qamid.utils.helpers.SwipeActions;

public class AppBar {
    protected static final int ID_AUTH_BUTTON = R.id.authorization_image_button;
    protected static final int ID_LOGOUT_ITEM = R.id.authorization_logout_menu_item;
    protected static final int ID_QUOTE_BUTTON = R.id.our_mission_image_button;
    protected static final int ID_MAIN_MENU_BUTTON = R.id.main_menu_image_button;
    protected static final int ID_TRADEMARK_IMAGE = R.id.trademark_image_view;
    protected static final String RES_MAIN_STR = getString(R.string.main);
    protected static final String RES_NEWS_STR = getString(R.string.news);

    public void assertTrademarkIsDisplayed() {
        reportAllureStep("Проверяем, что логотип отображается", () -> {

            onView(withId(ID_TRADEMARK_IMAGE))
                    .check(matches(isDisplayed()));

        });
    }

    public AppBar clickAuthButton() {
        reportAllureStep("Нажимаем кнопку «Авторизация» в AppBar", () -> {

            onView(withId(ID_AUTH_BUTTON))
                    .check(matches(isDisplayed()))
                    .perform(click());

        });
        return this;
    }

    public void clickLogout() {
        reportAllureStep("Нажимаем пункт «Выйти» в меню", () -> {

            onData(allOf(
                    instanceOf(MenuItem.class),
                    withMenuItemId(ID_LOGOUT_ITEM)))
                    .inRoot(isPlatformPopup())
                    .perform(click());

        });
    }

    public void assertLogoutMenuItemIsDisplayed() {
        reportAllureStep("Проверяем, что пункт «Выйти» отображается в меню", () -> {

            onData(allOf(
                    instanceOf(MenuItem.class),
                    withMenuItemId(ID_LOGOUT_ITEM)))
                    .inRoot(isPlatformPopup())
                    .check(matches(isDisplayed()));

        });
    }

    public AppBar clickQuoteButton() {
        reportAllureStep("Нажимаем кнопку «Цитаты» в AppBar", () -> {

            onView(withId(ID_QUOTE_BUTTON))
                    .check(matches(isDisplayed()))
                    .perform(click());

        });
        return this;
    }

    public AppBar clickMainMenuButton() {
        reportAllureStep("Нажимаем кнопку главного меню в AppBar", () -> {

            onView(withId(ID_MAIN_MENU_BUTTON))
                    .check(matches(isDisplayed()))
                    .perform(click());

        });
        return this;
    }

    public void clickMain() {
        reportAllureStep("Выбираем пункт «Главная» в меню навигации", () -> {

            onData(allOf(
                    instanceOf(MenuItem.class),
                    withMenuItemTitle(RES_MAIN_STR)))
                    .inRoot(isPlatformPopup())
                    .perform(click());

        });
    }

    public void clickNews() {
        reportAllureStep("Выбираем пункт «Новости» в меню навигации", () -> {

            onData(allOf(
                    instanceOf(MenuItem.class),
                    withMenuItemTitle(RES_NEWS_STR)))
                    .inRoot(isPlatformPopup())
                    .perform(click());

        });
    }

    private void checkItemIsDisplayedByTitle(String title) {
        onData(allOf(
                instanceOf(MenuItem.class),
                withMenuItemTitle(title)))
                .inRoot(isPlatformPopup())
                .check(matches(isDisplayed()));
    }

    public AppBar assertMainMenuItemMainIsDisplayed() {
        reportAllureStep("Проверяем, что пункт меню «Главная» отображается", () -> {

            checkItemIsDisplayedByTitle(RES_MAIN_STR);

        });
        return this;
    }

    public AppBar assertMainMenuItemNewsIsDisplayed() {
        reportAllureStep("Проверяем, что пункт меню «Новости» отображается", () -> {

            checkItemIsDisplayedByTitle(RES_NEWS_STR);

        });
        return this;
    }

    private void assertMainMenuItemState(String title, boolean expectedEnabled) {
        Espresso.onData(allOf(
                        instanceOf(MenuItem.class),
                        withMenuItemTitle(title),
                        withMenuItemEnabled(expectedEnabled)
                ))
                .inRoot(isPlatformPopup())
                .check(matches(isDisplayed()));
    }

    public AppBar assertMainMenuItemNewsIsActive() {
        reportAllureStep("Проверяем, что пункт меню «Новости» активен (можно нажать)", () -> {

            assertMainMenuItemState(RES_NEWS_STR, true);

        });
        return this;
    }

    public AppBar assertMainMenuItemNewsIsDisabled() {
        reportAllureStep("Проверяем, что пункт меню «Новости» неактивен (нельзя нажать)", () -> {

            assertMainMenuItemState(RES_NEWS_STR, false);

        });
        return this;
    }

    public AppBar assertMainMenuItemMainIsActive() {
        reportAllureStep("Проверяем, что пункт меню «Главная» активен (можно нажать)", () -> {

            assertMainMenuItemState(RES_MAIN_STR, true);

        });
        return this;
    }

    public AppBar assertMainMenuItemMainIsDisabled() {
        reportAllureStep("Проверяем, что пункт меню «Главная» неактивен (нельзя нажать)", () -> {

            assertMainMenuItemState(RES_MAIN_STR, false);

        });
        return this;
    }

    protected void performSwipeOnRefreshLayout(int refreshLayoutId) {
        androidx.test.espresso.Espresso.onView(
                        androidx.test.espresso.matcher.ViewMatchers.withId(refreshLayoutId)
                )
                .perform(SwipeActions.swipeDownRelaxed());
    }

    protected void checkButtonEnabledState(int viewId, boolean expectedEnabled) {
        if (expectedEnabled) {
            // Ожидаем, что кнопка включена
            onView(withId(viewId))
                    .check(matches(isEnabled()));
        } else {
            // Ожидаем, что кнопка НЕ включена
            onView(withId(viewId))
                    .check(matches(not(isEnabled())));
        }
    }

    protected DataInteraction findMenuItemByTitle(String title) {
        return onData(allOf(
                instanceOf(android.view.MenuItem.class),
                withMenuItemTitle(title)
        )).inRoot(isPlatformPopup());
    }
}
