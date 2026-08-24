package ru.edu.qamid.tests;

import static ru.edu.qamid.utils.data.DataGenerator.generateWithTimestamp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.Story;
import ru.edu.qamid.screens.MainScreen;
import ru.edu.qamid.screens.NewsScreen;
import ru.edu.qamid.steps.ApiSteps;
import ru.edu.qamid.steps.AuthSteps;
import ru.edu.qamid.utils.data.TestData;


@Feature("Экран «Главная»")
public class MainScreenTest extends BaseE2eEspressoTest {
    private ApiSteps apiSteps;
    private MainScreen mainScreen;

    @Before
    public void setup() throws IOException {
        apiSteps = prepareIsolatedNewsData();

        AuthSteps authSteps = new AuthSteps();
        authSteps.loginStep(TestData.LOGIN, TestData.PASSWORD);
        mainScreen = onMainScreen();
    }

    @After
    public void teardown() {
        safeCleanupSteps(apiSteps);
    }

    @Story("Отображение элементов экрана")
    @Description("Отображение блока с новостями")
    @Test
    public void shouldDisplayedNewsContainer() {
        mainScreen.assertNewsContainerIsDisplayed();
    }

    @Story("Переход на экран «Новости», через блок новостей")
    @Description("Переход по ссылке «Все новости» в блоке новостей на экран «Новости»")
    @Test
    public void shouldOpenNewsScreenThroughContainer() {
        NewsScreen newsScreen = new NewsScreen();

        mainScreen.clickAllNewsButton();

        newsScreen.assertNewsScreenIsDisplayed();
    }

    @Story("Обновление экрана")
    @Description("После обновления экрана свайпом вниз отредактированная новость меняется")
    @Test
    public void listShouldRefreshAfterSwipe() throws IOException {

//        // Изменения в viewmodel/NewsViewModel.kt
//        private suspend fun internalOnRefresh() {
//
//            EspressoIdlingResource.increment()
//
//            try {
//                newsRepository.refreshNews()
//                newsListUpdatedEvent.emit(Unit)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                loadNewsExceptionEvent.emit(Unit)
//
//            } finally {
//                EspressoIdlingResource.decrement()
//            }
//
//        }

        String initialTitle = generateWithTimestamp("Test_title_");
        String initialDescription = generateWithTimestamp("Test_description_");
        String newTitle = initialTitle+  "_edited";
        String newDescription = initialDescription + "_edited";


        // Создаём нужную новость через API
        ru.edu.qamid.utils.data.network.NewsItem createdWithEdit = apiSteps.createNewsAndSwapContent(
                initialTitle,
                initialDescription,
                newTitle,
                newDescription
        );
        // Делаем свайп
        mainScreen
                .performSwipeToRefresh()
                .assertNewsContainerIsDisplayed()
                .assertNewsListIsDisplayed();
        // Ищем в списке новость
        mainScreen.assertCardByTwoTexts(
                initialTitle,
                initialDescription
        );

        // Редактируем созданную новость через API
        apiSteps.editExistingNewsFromObject(createdWithEdit);

        // Делаем свайп
        mainScreen
                .performSwipeToRefresh();

        // Ищем в списке изменённую новость
        mainScreen
                .assertNewsContainerIsDisplayed()
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(
                        newTitle,
                        newDescription
                );
    }

}
