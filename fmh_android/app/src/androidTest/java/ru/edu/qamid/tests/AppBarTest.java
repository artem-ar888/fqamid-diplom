package ru.edu.qamid.tests;

import org.junit.Before;
import org.junit.Test;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.Story;
import ru.edu.qamid.screens.AuthScreen;
import ru.edu.qamid.screens.MainScreen;
import ru.edu.qamid.screens.NewsCPScreen;
import ru.edu.qamid.screens.NewsScreen;
import ru.edu.qamid.screens.QuoteScreen;
import ru.edu.qamid.steps.AuthSteps;
import ru.edu.qamid.utils.data.TestData;

@Feature("Панель AppBar")
public class AppBarTest extends BaseE2eEspressoTest{

    @Before
    public void setup() {
        AuthSteps authSteps = new AuthSteps();
        authSteps.loginStep(TestData.LOGIN, TestData.PASSWORD);
        new MainScreen().assertMainScreenIsDisplayed();
    }

    @Story("Отображение элементов")
    @Description("Логотип отображается на экране «Главная»")
    @Test
    public void shouldDisplayedTrademark() {
        MainScreen mainScreen = onMainScreen();
        mainScreen.assertTrademarkIsDisplayed();
    }

    @Story("Работа меню Logout")
    @Description("Нажатие на кнопку Logout открывает меню с пунктом «Выйти»")
    @Test
    public void shouldOpenLogoutMenu() {
        MainScreen mainScreen = onMainScreen();
        mainScreen
                .clickAuthButton()
                .assertLogoutMenuItemIsDisplayed();
    }

    @Story("Работа Главного меню")
    @Description("Отображение пунктов меню")
    @Test
    public void shouldOpenMainMenuWithItems() {
        MainScreen mainScreen = onMainScreen();
        mainScreen
                .clickMainMenuButton()
                .assertMainMenuItemMainIsDisplayed()
                .assertMainMenuItemNewsIsDisplayed();
    }

    @Story("Работа Главного меню")
    @Description("Пункт меню «Главная» не активен на экране «Главная»")
    @Test
    public void shouldBeDisabledItemMainInMainMenuOnMainScreen() {
        MainScreen mainScreen = onMainScreen();
        mainScreen
                .clickMainMenuButton()
                .assertMainMenuItemMainIsDisabled();
    }

    @Story("Работа Главного меню")
    @Description("Пункт меню «Новости» активен на экране «Главная»")
    @Test
    public void shouldBeActiveItemNewsInMainMenuOnMainScreen() {
        MainScreen mainScreen = onMainScreen();
        mainScreen
                .clickMainMenuButton()
                .assertMainMenuItemNewsIsActive();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Цитаты» с экрана «Главная»")
    @Test
    public void shouldOpenQuoteScreenFromMainScreen() {
        QuoteScreen quoteScreen = onQuoteScreen();
        quoteScreen.checkQuoteScreenTitle();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Новости» с экрана «Главная»")
    @Test
    public void shouldOpenNewsScreenFromMainScreen() {
        NewsScreen newsScreen = onNewsScreen();
        newsScreen.assertNewsScreenIsDisplayed();
    }

    @Story("Работа Главного меню")
    @Description("Пункт меню «Главная» активен на экране «Новости»")
    @Test
    public void shouldBeActiveItemMainInMainMenuOnNewsScreen() {
        NewsScreen newsScreen = onNewsScreen();
        newsScreen
                .clickMainMenuButton()
                .assertMainMenuItemMainIsActive();
    }

    @Story("Работа Главного меню")
    @Description("Пункт меню «Новости» не активен на экране «Новости»")
    @Test
    public void shouldBeDisabledItemNewsInMainMenuOnNewsScreen() {
        NewsScreen newsScreen = onNewsScreen();
        newsScreen
                .clickMainMenuButton()
                .assertMainMenuItemNewsIsDisabled();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Главная» с экрана «Новости»")
    @Test
    public void shouldOpenMainScreenFromNewsScreen() {
        NewsScreen newsScreen = onNewsScreen();
        newsScreen
                .clickMainMenuButton()
                .clickMain();
        new MainScreen().assertMainScreenIsDisplayed();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Цитаты» с экрана «Новости»")
    @Test
    public void shouldOpenQuoteScreenFromNewsScreen() {
        NewsScreen newsScreen = onNewsScreen();
        newsScreen.clickQuoteButton();

        new QuoteScreen().checkQuoteScreenTitle();
    }

    @Story("Работа Главного меню")
    @Description("Пункт меню «Главная» активен на экране «Цитаты»")
    @Test
    public void shouldBeActiveItemMainInMainMenuOnQuoteScreen() {
        QuoteScreen quoteScreen = onQuoteScreen();
        quoteScreen
                .clickMainMenuButton()
                .assertMainMenuItemMainIsActive();
    }

    @Story("Работа Главного меню")
    @Description("Пункт меню «Новости» активен на экране «Цитаты»")
    @Test
    public void shouldBeActiveItemNewsInMainMenuOnQuoteScreen() {
        QuoteScreen quoteScreen = onQuoteScreen();
        quoteScreen
                .clickMainMenuButton()
                .assertMainMenuItemNewsIsActive();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Главная» с экрана «Цитаты»")
    @Test
    public void shouldOpenMainScreenFromQuoteScreen() {
        QuoteScreen quoteScreen = onQuoteScreen();
        quoteScreen
                .clickMainMenuButton()
                .clickMain();
        new MainScreen().assertMainScreenIsDisplayed();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Новости» с экрана «Цитаты»")
    @Test
    public void shouldOpenNewsScreenFromQuoteScreen() {
        QuoteScreen quoteScreen = onQuoteScreen();
        quoteScreen
                .clickMainMenuButton()
                .clickNews();

        new NewsScreen().assertNewsScreenIsDisplayed();
    }

    @Story("Работа Главного меню")
    @Description("Пункт меню «Главная» активен на экране «Панель управления»")
    @Test
    public void shouldBeActiveItemMainInMainMenuOnNewsCPScreen() {
        NewsCPScreen newsCPScreen = onNewsCPScreen();
        newsCPScreen
                .clickMainMenuButton()
                .assertMainMenuItemMainIsActive();
    }

    @Story("Работа Главного меню")
    @Description("Пункт меню «Новости» активен на экране «Панель управления»")
    @Test
    public void shouldBeActiveItemNewsInMainMenuOnNewsCPScreen() {
        NewsCPScreen newsCPScreen = onNewsCPScreen();
        newsCPScreen
                .clickMainMenuButton()
                .assertMainMenuItemNewsIsActive();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Главная» с экрана «Панель управления»")
    @Test
    public void shouldOpenMainScreenFromNewsCPScreen() {
        NewsCPScreen newsCPScreen = onNewsCPScreen();
        newsCPScreen
                .clickMainMenuButton()
                .clickMain();
        new MainScreen().assertMainScreenIsDisplayed();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Новости» с экрана «Панель управления»")
    @Test
    public void shouldOpenNewsScreenFromNewsCPScreen() {
        NewsCPScreen newsCPScreen = onNewsCPScreen();
        newsCPScreen
                .clickMainMenuButton()
                .clickNews();

        new NewsScreen().assertNewsScreenIsDisplayed();
    }

    @Story("Переходы с одного экрана на другой через AppBar")
    @Description("Переход на экран «Цитаты» с экрана «Панель управления»")
    @Test
    public void shouldOpenQuoteScreenFromNewsCPScreen() {
        NewsCPScreen newsCPScreen = onNewsCPScreen();
        newsCPScreen.clickQuoteButton();

        new QuoteScreen().checkQuoteScreenTitle();
    }

    @Story("Работа меню Logout")
    @Description("При выборе пункта «Выйти» пользователь перенаправляется на экран авторизации")
    @Test
    public void logout() {
        AuthSteps authSteps = new AuthSteps();
        AuthScreen authScreen = new AuthScreen();

        authSteps.logoutStep();

        authScreen.checkScreenTitle();
    }
}
