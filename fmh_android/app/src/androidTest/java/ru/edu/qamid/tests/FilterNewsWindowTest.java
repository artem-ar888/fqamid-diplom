package ru.edu.qamid.tests;

import static ru.edu.qamid.utils.data.DataGenerator.generateDateStringDaysAgo;

import org.junit.Before;
import org.junit.Test;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.Story;
import ru.edu.qamid.screens.FilterCPNewsWindow;
import ru.edu.qamid.screens.FilterNewsWindow;
import ru.edu.qamid.screens.NewsCPScreen;
import ru.edu.qamid.screens.NewsScreen;
import ru.edu.qamid.steps.AuthSteps;
import ru.edu.qamid.utils.data.TestData;

@Feature("Окно «Фильтровать новости»")
public class FilterNewsWindowTest extends BaseE2eEspressoTest {

    @Before
    public void setup() {
        AuthSteps authSteps = new AuthSteps();
        authSteps.loginStep(TestData.LOGIN, TestData.PASSWORD);
        onMainScreen();
    }

    /**
     * Фабрика: возвращает готовый FilterNewsWindow
     */
    private FilterNewsWindow startFromNewsScreen() {
        onNewsScreen().clickFilterNewsButton();

        FilterNewsWindow filterNewsWindow = new FilterNewsWindow();
        filterNewsWindow.assertFilterNewsIsDisplayed();
        return filterNewsWindow;
    }

    /**
     * Фабрика: переходит из списка новостей на Панель управления новостями
     * и возвращает готовый FilterCPNewsWindow
     */
    private FilterCPNewsWindow startFromNewsCPScreen() {
        onNewsCPScreen().clickFilterNewsButton();

        FilterCPNewsWindow filterCPNewsWindow = new FilterCPNewsWindow();
        filterCPNewsWindow.assertFilterNewsIsDisplayed();
        return filterCPNewsWindow;
    }

    @Story("Применение фильтрации")
    @Description("Применение фильтра новостей с указанием категории и диапазона дат, отображается экран «Новости»")
    @Test
    public void shouldApplyingCategoryAndDateFilterAndShowsNewsScreen() {
        String category = "Объявление";
        String startDate = generateDateStringDaysAgo(2);
        String endDate = generateDateStringDaysAgo(1);

        startFromNewsScreen()
                .selectCategory(category)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .apply();

        new NewsScreen().assertNewsScreenIsDisplayed();
    }

    @Story("Работа элементов окна")
    @Description("Отмена фильтра новостей, отображается экран «Новости»")
    @Test
    public void shouldCancelingFilterNewsWindowAndShowsNewsScreen() {
        startFromNewsScreen()
                .cancel();

        new NewsScreen().assertNewsScreenIsDisplayed();
    }

    @Story("Применение фильтрации")
    @Description("Применение фильтра новостей с указанием категории и диапазона дат, отображается экран «Панель управления»")
    @Test
    public void shouldApplyingCategoryAndDateFilterAndShowsNewsCPScreen() {
        String category = "Объявление";
        String startDate = generateDateStringDaysAgo(2);
        String endDate = generateDateStringDaysAgo(1);


        startFromNewsCPScreen()
                .selectCategory(category)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .apply();

        new NewsCPScreen().assertNewsCPScreenIsDisplayed();
    }

    @Story("Применение фильтрации")
    @Description("Применение фильтра новостей с использованием чекбоксов активности, отображается экран «Панель управления»")
    @Test
    public void shouldApplyingStatusFilterAndShowsNewsCPScreen() {
        startFromNewsCPScreen()
                .ensureActiveCheckbox(true)
                .ensureInactiveCheckbox(false)
                .apply();

        new NewsCPScreen().assertNewsCPScreenIsDisplayed();
    }

    @Story("Отображение элементов окна")
    @Description("Проверяем, что чекбокс «Активна» отмечен")
    @Test
    public void shouldCheckedActiveCheckbox() {
        startFromNewsCPScreen()
                .assertActiveCheckboxChecked(true);
    }

    @Story("Отображение элементов окна")
    @Description("Проверяем, что чекбокс «Не активна» отмечен")
    @Test
    public void shouldCheckedInactiveCheckbox() {
        startFromNewsCPScreen()
                .assertInactiveCheckboxChecked(true);
    }

}
