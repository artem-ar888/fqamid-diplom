package ru.edu.qamid.tests;

import static ru.edu.qamid.utils.data.DataGenerator.dateStringToUtcUnixTimestamp;
import static ru.edu.qamid.utils.data.DataGenerator.generateDateStringDaysAgo;
import static ru.edu.qamid.utils.data.DataGenerator.generateWithTimestamp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.Story;
import ru.edu.qamid.screens.CreateEditNewsForm;
import ru.edu.qamid.screens.FilterNewsWindow;
import ru.edu.qamid.screens.NewsCPScreen;
import ru.edu.qamid.screens.NewsScreen;
import ru.edu.qamid.steps.ApiSteps;
import ru.edu.qamid.steps.AuthSteps;
import ru.edu.qamid.utils.data.DataGenerator;
import ru.edu.qamid.utils.data.NewsItem;
import ru.edu.qamid.utils.data.TestData;

@Feature("Экран «Новости»")
public class NewsScreenTest extends BaseE2eEspressoTest {
    private ApiSteps apiSteps;
    private NewsScreen newsScreen;

    @Before
    public void setup() throws IOException {
        apiSteps = prepareIsolatedNewsData();

        AuthSteps authSteps = new AuthSteps();
        authSteps.loginStep(TestData.LOGIN, TestData.PASSWORD);
        onMainScreen();
    }

    @After
    public void teardown() {
        safeCleanupSteps(apiSteps);
    }

    @Story("Работа элементов экрана")
    @Description("Открытие экрана «Панель управления» при нажатии кнопка редактирования новости")
    @Test
    public void shouldOpenNewsControlPanel() {
        NewsCPScreen newsCPScreen = new NewsCPScreen();
        newsScreen = onNewsScreen();

        newsScreen.clickNewsEditButton();

        newsCPScreen.assertNewsCPScreenIsDisplayed();
    }

    @Story("Отображение элементов экрана")
    @Description("Отображение списка новостей")
    @Test
    public void shouldBeDisplayedNewsList() {
        newsScreen = onNewsScreen();
        newsScreen.assertNewsListIsDisplayed();
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
        String newTitle = initialTitle + "_edited";
        String newDescription = initialDescription + "_edited";

        // Создаём нужную новость через API
        ru.edu.qamid.utils.data.network.NewsItem createdWithEdit = apiSteps.createNewsAndSwapContent(
                initialTitle,
                initialDescription,
                newTitle,
                newDescription
        );
        newsScreen = onNewsScreen();

        // Ищем в списке новость
        newsScreen.assertCardByTwoTexts(
                initialTitle,
                initialDescription
        );

        // Редактируем созданную новость через API
        apiSteps.editExistingNewsFromObject(createdWithEdit);

        // Делаем свайп
        newsScreen
                .performSwipeToRefresh();

        // Ищем в списке изменённую новость
        newsScreen
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(
                        newTitle,
                        newDescription
                );
    }

    @Story("Отображение элементов экрана")
    @Description("Скроллинг экрана до 8ой новости в списке")
    @Test
    public void shouldScrollToEighthNews() throws IOException {
        int countOfNews = 8;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");


        String newsTargetTitle = titlePrefix + 8;
        String newsTargetDescription = descriptionPrefix + 8;

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsScreen = onNewsScreen();

        newsScreen.assertCardByTwoTexts(
                newsTargetTitle,
                newsTargetDescription
        );
    }

    @Story("Отображение элементов экрана")
    @Description("Скроллинг экрана до 9ой новости в списке")
    // Тест падает из-за того, что приложение не грузит больше восьми новостей
    @Test
    public void shouldScrollToNinthNews() throws IOException {
        int countOfNews = 9;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");


        String newsTargetTitle = titlePrefix + 9;
        String newsTargetDescription = descriptionPrefix + 9;

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsScreen = onNewsScreen();

        newsScreen.assertCardByTwoTexts(
                newsTargetTitle,
                newsTargetDescription
        );
    }

    @Story("Отображение элементов экрана")
    @Description("Отображение заголовка у новости")
    @Test
    public void shouldDisplayedNewsTitle() throws IOException {
        int countOfNews = 1;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");


        String newsTargetTitle = titlePrefix + 1;
        String newsTargetDescription = descriptionPrefix + 1;

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsScreen = onNewsScreen();

        newsScreen.assertCardByTwoTexts(
                newsTargetTitle,
                newsTargetDescription
        );
        newsScreen.assertNewsTitleIsDisplayed(newsTargetTitle);
    }

    @Story("Отображение элементов экрана")
    @Description("Отображение описания у новости")
    @Test
    public void shouldDisplayedNewsDescription() throws IOException {
        int countOfNews = 1;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");


        String newsTargetTitle = titlePrefix + 1;
        String newsTargetDescription = descriptionPrefix + 1;

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsScreen = onNewsScreen();

        newsScreen.openCardByTwoTexts(
                newsTargetTitle,
                newsTargetDescription
        );
        newsScreen.assertNewsDescriptionIsDisplayed(newsTargetDescription);
    }

    @Story("Отображение элементов экрана")
    @Description("Удалённая новость не отображается в списке")
    @Test
    public void shouldNotExistDeletedNews() throws IOException {
        int countOfNews = 1;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");

        String newsTargetTitle = titlePrefix + 1;
        String newsTargetDescription = descriptionPrefix + 1;

        NewsCPScreen newsCPScreen = new NewsCPScreen();

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsScreen = onNewsScreen();

        newsScreen.
                assertCardByTwoTexts(
                        newsTargetTitle,
                        newsTargetDescription
                )
                .clickNewsEditButton();
        newsCPScreen
                .assertNewsCPScreenIsDisplayed();
        newsCPScreen
                .deleteCardByTitle(newsTargetTitle)
                .clickMainMenuButton()
                .clickNews();

        newsScreen
                .assertNewsDoesNotExist(
                        newsTargetTitle,
                        newsTargetDescription
                );
    }

    @Story("Отображение элементов экрана")
    @Description("Неактивная новость не отображается в списке")
    @Test
    public void shouldNotVisibleInactiveNews() throws IOException {
        int countOfNews = 1;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");


        String newsTargetTitle = titlePrefix + 1;
        String newsTargetDescription = descriptionPrefix + 1;

        NewsCPScreen newsCPScreen = new NewsCPScreen();
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsScreen = onNewsScreen();

        newsScreen.
                assertCardByTwoTexts(
                        newsTargetTitle,
                        newsTargetDescription
                )
                .clickNewsEditButton();
        newsCPScreen
                .assertNewsCPScreenIsDisplayed();
        int humanPosition = newsCPScreen.findPositionByTitle(newsTargetTitle) + 1;
        newsCPScreen
                .clickNewsEditButtonAtPosition(humanPosition);
        createEditNewsForm
                .assertIsEditingForm()
                .ensureSwitchActiveState(false)
                .save();
        newsCPScreen
                .assertNewsCPScreenIsDisplayed()
                .clickMainMenuButton()
                .clickNews();

        newsScreen
                .assertNewsDoesNotExist(
                        newsTargetTitle,
                        newsTargetDescription
                );
    }

    @Story("Работа элементов экрана")
    @Description("Работка сортировки по датам публикации")
    @Test
    public void shouldSortNewsByPublishDate() throws IOException {
        int countOfNews = 8;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsScreen = onNewsScreen();

        NewsItem firstNews = newsScreen.getNewsAtPosition(1);
        NewsItem secondNews = newsScreen.getNewsAtPosition(2);
        long dateFirst = dateStringToUtcUnixTimestamp(firstNews.getPublishDateText());
        long dateSecond = dateStringToUtcUnixTimestamp(secondNews.getPublishDateText());
        Assert.assertTrue(
                "Первая дата должна быть больше второй",
                dateFirst > dateSecond
        );

        newsScreen
                .clickSortNewsButton()
                .assertNewsListIsDisplayed();

        NewsItem firstNewsAfterSort = newsScreen.getNewsAtPosition(1);
        NewsItem secondNewsAfterSort = newsScreen.getNewsAtPosition(2);
        long dateFirstAfterSort = dateStringToUtcUnixTimestamp(firstNewsAfterSort.getPublishDateText());
        long dateSecondAfterSort = dateStringToUtcUnixTimestamp(secondNewsAfterSort.getPublishDateText());
        Assert.assertTrue(
                "Первая дата должна быть меньше второй",
                dateFirstAfterSort < dateSecondAfterSort
        );
    }

    @Story("Работа фильтрации новостей")
    @Description("Фильтрация новостей со вчерашнего дня по сегодняшний день")
    @Test
    public void shouldFilterNewsToYesterdayAndToday() throws IOException {
        int countOfNews = 3;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");

        String yesterdayDate = generateDateStringDaysAgo(1);
        String todayDate = generateDateStringDaysAgo(0);

        String firstNewsTargetTitle = titlePrefix + 1;
        String firstNewsTargetDescription = descriptionPrefix + 1;
        String secondNewsTargetTitle = titlePrefix + 2;
        String secondNewsTargetDescription = descriptionPrefix + 2;
        String thirdNewsTargetTitle = titlePrefix + 3;
        String thirdNewsTargetDescription = descriptionPrefix + 3;

        FilterNewsWindow filterNewsWindow = new FilterNewsWindow();

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsScreen = onNewsScreen();
        newsScreen
                .assertCardByTwoTexts(thirdNewsTargetTitle,thirdNewsTargetDescription)
                .clickFilterNewsButton();

        filterNewsWindow
                .assertFilterNewsIsDisplayed()
                .setStartDate(yesterdayDate)
                .setEndDate(todayDate)
                .apply();

        newsScreen
                .assertNewsScreenIsDisplayed()
                .assertCardByTwoTexts(firstNewsTargetTitle,firstNewsTargetDescription)
                .assertCardByTwoTexts(secondNewsTargetTitle,secondNewsTargetDescription)
                .assertNewsDoesNotExist(thirdNewsTargetTitle,thirdNewsTargetDescription);
    }

    @Story("Работа фильтрации новостей")
    @Description("Фильтрация новостей по категории")
    @Test
    public void shouldFilterNewsByCategory() throws IOException {
        String targetCategory = "Объявление";

        int countOfNews = 2;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.randomAlphanumeric(16) + "_";


        String firstNewsTargetTitle = titlePrefix + 1;
        String firstNewsTargetDescription = descriptionPrefix + 1;
        String secondNewsTargetTitle = titlePrefix + 2;
        String secondNewsTargetDescription = descriptionPrefix + 2;

        FilterNewsWindow filterNewsWindow = new FilterNewsWindow();

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );
        newsScreen = onNewsScreen();
        newsScreen
                .assertCardByTwoTexts(firstNewsTargetTitle,firstNewsTargetDescription)
                .assertCardByTwoTexts(secondNewsTargetTitle,secondNewsTargetDescription)
                .clickFilterNewsButton();

        filterNewsWindow
                .assertFilterNewsIsDisplayed()
                .selectCategory(targetCategory)
                .apply();

        newsScreen
                .assertNewsScreenIsDisplayed()
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(firstNewsTargetTitle,firstNewsTargetDescription)
                .assertNewsDoesNotExist(secondNewsTargetTitle,secondNewsTargetDescription);
    }

    @Story("Работа фильтрации новостей")
    @Description("Фильтрация всех новостей по категории")
    // Тест падает из-за того, что фильтруются только звгруженные новости с первой страницы
    @Test
    public void shouldFilterAllNewsByCategory() throws IOException {
        String targetCategory = "Объявление";

        int countOfNews = 9;
        String titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        String descriptionPrefix = DataGenerator.randomAlphanumeric(16) + "_";


        String firstNewsTargetTitle = titlePrefix + 1;
        String firstNewsTargetDescription = descriptionPrefix + 1;
        String ninthNewsTargetTitle = titlePrefix + 9;
        String ninthNewsTargetDescription = descriptionPrefix + 9;

        FilterNewsWindow filterNewsWindow = new FilterNewsWindow();

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );
        newsScreen = onNewsScreen();
        newsScreen
                .assertCardByTwoTexts(firstNewsTargetTitle,firstNewsTargetDescription)
                .clickFilterNewsButton();

        filterNewsWindow
                .assertFilterNewsIsDisplayed()
                .selectCategory(targetCategory)
                .apply();

        newsScreen
                .assertNewsScreenIsDisplayed()
                .assertCardByTwoTexts(firstNewsTargetTitle,firstNewsTargetDescription)
                .assertCardByTwoTexts(ninthNewsTargetTitle,ninthNewsTargetDescription);
    }





//    @Test
//    public void getSecondNews() {
//        int targetHumanPosition = 2;
//
//        newsScreen = onNewsScreen();
//        newsScreen.openCardAtPosition(targetHumanPosition);
//        NewsItem item = newsScreen.getNewsAtPosition(targetHumanPosition);
//
//        Log.d("EspressoTest", "=== Данные карточки № " + targetHumanPosition + " ===");
//        Log.d("EspressoTest", "Title: " + item.getTitleText());
//        Log.d("EspressoTest", "PublishDate: " + item.getPublishDateText());
//        Log.d("EspressoTest", "Category: " + item.getNewsCategory());
//        Log.d("EspressoTest", "Description: " + item.getDescriptionText());
//        Log.d("EspressoTest", "=========================");
//    }

}
