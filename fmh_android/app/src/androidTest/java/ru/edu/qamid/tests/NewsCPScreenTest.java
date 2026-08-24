package ru.edu.qamid.tests;

import static ru.edu.qamid.utils.data.DataGenerator.dateStringToUtcUnixTimestamp;
import static ru.edu.qamid.utils.data.DataGenerator.generateDateStringDaysFuture;
import static ru.edu.qamid.utils.data.DataGenerator.generateRelativeUnixTimestampSeconds;
import static ru.edu.qamid.utils.data.DataGenerator.generateTimeStringMinutesOffset;
import static ru.edu.qamid.utils.data.DataGenerator.generateWithTimestamp;
import static ru.edu.qamid.utils.data.DataGenerator.getCategoryName;
import static ru.edu.qamid.utils.data.DataGenerator.randomAlphanumeric;
import static ru.edu.qamid.utils.data.DataGenerator.randomIntBetween;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.Story;
import ru.edu.qamid.screens.CreateEditNewsForm;
import ru.edu.qamid.screens.FilterCPNewsWindow;
import ru.edu.qamid.screens.NewsCPScreen;
import ru.edu.qamid.steps.ApiSteps;
import ru.edu.qamid.steps.AuthSteps;
import ru.edu.qamid.utils.data.AdminNewsItem;
import ru.edu.qamid.utils.data.DataGenerator;
import ru.edu.qamid.utils.data.TestData;

@Feature("Экран «Панель управления» (новостями)")
public class NewsCPScreenTest extends BaseE2eEspressoTest {

    private ApiSteps apiSteps;
    private NewsCPScreen newsCPScreen;
    private String titlePrefix;
    private String descriptionPrefix;

    @Before
    public void setup() throws IOException {
        int countOfNews = 9;
        titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");

        apiSteps = prepareIsolatedNewsData();

        AuthSteps authSteps = new AuthSteps();
        authSteps.loginStep(TestData.LOGIN, TestData.PASSWORD);
        onMainScreen();

        apiSteps.seedDatabaseWithTimeSeriesNews(
                countOfNews,
                titlePrefix,
                descriptionPrefix
        );

        newsCPScreen = onNewsCPScreen();
    }

    @After
    public void teardown() {
        safeCleanupSteps(apiSteps);
    }

    @Story("Отображение элементов экрана")
    @Description("Отображение списка новостей")
    @Test
    public void shouldBeDisplayedNewsList() {
        newsCPScreen.assertNewsListIsDisplayed();
    }

    @Story("Отображение элементов экрана")
    @Description("Отображение даты публикации у новостей")
    @Test
    public void shouldBeDisplayedNewsPublishedText() {
        newsCPScreen.checkNewsPublishedTextIsDisplayed();
    }

    @Story("Работа элементов экрана")
    @Description("Открытие окна удаления при нажатии кнопки «Удаление» у новости")
    @Test
    public void shouldOpenNewsDeleteWindow() {
        int targetHumanPosition = 1;

//        newsCPScreen.clickNewsDeleteButtonAtPosition(targetHumanPosition);
        newsCPScreen
                .clickNewsDeleteButtonAtPosition(targetHumanPosition)
                .verifyDeleteConfirmationDialog();
    }

    @Story("Работа элементов экрана")
    @Description("Работка сортировки по датам публикации")
    @Test
    public void shouldSortNewsByPublishDate() throws IOException {
        AdminNewsItem firstNews = newsCPScreen.getAdminNewsAtPosition(1);
        AdminNewsItem secondNews = newsCPScreen.getAdminNewsAtPosition(2);
        long dateFirst = dateStringToUtcUnixTimestamp(firstNews.getPublishDateText());
        long dateSecond = dateStringToUtcUnixTimestamp(secondNews.getPublishDateText());
        Assert.assertTrue(
                "Первая дата должна быть больше второй",
                dateFirst > dateSecond
        );

        newsCPScreen
                .clickSortNewsButton()
                .assertNewsListIsDisplayed();

        AdminNewsItem firstNewsAfterSort = newsCPScreen.getAdminNewsAtPosition(1);
        AdminNewsItem secondNewsAfterSort = newsCPScreen.getAdminNewsAtPosition(2);
        long dateFirstAfterSort = dateStringToUtcUnixTimestamp(firstNewsAfterSort.getPublishDateText());
        long dateSecondAfterSort = dateStringToUtcUnixTimestamp(secondNewsAfterSort.getPublishDateText());
        Assert.assertTrue(
                "Первая дата должна быть меньше второй",
                dateFirstAfterSort < dateSecondAfterSort
        );
    }

    @Story("Работа элементов экрана")
    @Description("Открытие окна «Фильтровать новости»")
    @Test
    public void shouldOpenFilterNewsWindow() {
        FilterCPNewsWindow filterCPNewsWindow = new FilterCPNewsWindow();

        newsCPScreen.clickFilterNewsButton();

        filterCPNewsWindow.assertFilterNewsIsDisplayed();
    }

    @Story("Работа элементов экрана")
    @Description("Открытие формы «Создание новости»")
    @Test
    public void shouldOpenCreateNewsForm() {
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.clickCreateNewsButton();

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsCreatingForm();
    }

    @Story("Работа элементов экрана")
    @Description("Открытие формы «Редактирование новости» при нажатии кнопки «Редактирования» у новости")
    @Test
    public void shouldOpenEditNewsForm() {
        int targetHumanPosition = 1;
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.clickNewsEditButtonAtPosition(targetHumanPosition);

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsEditingForm();
    }

    @Story("Отображение элементов экрана")
    @Description("Отображение заголовка у новости")
    @Test
    public void shouldDisplayedNewsTitle() {
        String newsTargetTitle = titlePrefix + 1;
        String newsTargetDescription = descriptionPrefix + 1;

        newsCPScreen.assertCardByTwoTexts(
                newsTargetTitle,
                newsTargetDescription
        );

        newsCPScreen.assertNewsTitleIsDisplayed(newsTargetTitle);
    }

    @Story("Отображение элементов экрана")
    @Description("Отображение описания у новости")
    @Test
    public void shouldDisplayedNewsDescription() {
        String newsTargetTitle = titlePrefix + 1;
        String newsTargetDescription = descriptionPrefix + 1;

        newsCPScreen.openCardByTwoTexts(
                newsTargetTitle,
                newsTargetDescription
        );

        newsCPScreen.assertNewsDescriptionIsDisplayed(newsTargetDescription);
    }

    @Story("Удаление новости")
    @Description("Успешное удаление четвёртой новости")
    @Test
    public void shouldDeleteFourthNews() {
        int targetHumanPosition = 4;
        AdminNewsItem item = newsCPScreen.getAdminNewsAtPosition(targetHumanPosition);

        newsCPScreen
                .clickNewsDeleteButtonAtPosition(targetHumanPosition)
                .confirmDelete();

        // Поднимаемся вверх списка и делаем свайп для обновления экрана
        newsCPScreen.getAdminNewsAtPosition(1);
        newsCPScreen.performSwipeToRefresh();

        newsCPScreen.assertNewsDoesNotExist(
                item.getTitleText(),
                item.getDescriptionText()
        );
    }

    @Story("Удаление новости")
    @Description("Отмена удаления новости")
    @Test
    public void shouldBeCancelledDeletionFirstNews() {
        int targetHumanPosition = 1;
        AdminNewsItem item = newsCPScreen.getAdminNewsAtPosition(targetHumanPosition);

        newsCPScreen
                .clickNewsDeleteButtonAtPosition(targetHumanPosition)
                .cancelDelete();

        // Поднимаемся вверх списка и делаем свайп для обновления экрана
        newsCPScreen.getAdminNewsAtPosition(1);
        newsCPScreen.performSwipeToRefresh();

        newsCPScreen.assertCardByTwoTexts(
                item.getTitleText(),
                item.getDescriptionText()
        );
    }

    @Story("Обновление экрана")
    @Description("После обновления экрана свайпом вниз удалённая новость пропадает из списка")
    @Test
    public void listShouldRefreshAfterSwipe() throws IOException {

//        // Изменения в viewmodel/NewsControlPanelViewModel.kt
//        private suspend fun internalOnRefresh() {
//
//            EspressoIdlingResource.increment()
//
//            try {
//                newsRepository.refreshNews()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                loadNewsExceptionEvent.emit(Unit)
//            } finally {
//
//                EspressoIdlingResource.decrement()
//
//            }
//        }

        String title = generateWithTimestamp("News_to_Delete_");
        String description = "News_to_Delete_" + randomAlphanumeric(8);
        long tomorrowDate = generateRelativeUnixTimestampSeconds(TestData.DAY);

        // Создаём нужную новость через API
        ru.edu.qamid.utils.data.network.NewsItem createdNews = apiSteps.createNewsWithTitleDescriptionAndDate(
                title,
                description,
                tomorrowDate
        );

        // Делаем свайп
        newsCPScreen
                .performSwipeToRefresh()
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(title, description);

        apiSteps.deleteExistingNewsFromObject(createdNews);
        // Делаем свайп
        newsCPScreen
                .performSwipeToRefresh()
                .assertNewsListIsDisplayed()
                .assertNewsDoesNotExist(title, description);
    }

    @Story("Редактирование новости")
    @Description("Отредактированная первая новость отображается в списке новостей")
    @Test
    public void shouldEditFirstNews() {
        int targetHumanPosition = 1;
        String editTitle = generateWithTimestamp("Edited_News_");
        String editDescription = "Edited_Description_" + randomAlphanumeric(8);
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.assertNewsDoesNotExist(
                editTitle,
                editDescription
        );
        newsCPScreen.clickNewsEditButtonAtPosition(targetHumanPosition);

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsEditingForm()
                .enterNewsTitle(editTitle)
                .enterNewsDescription(editDescription)
                .save();

        newsCPScreen
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(
                        editTitle,
                        editDescription
                );
    }

    @Story("Создание новости")
    @Description("Созданная новость отображается в списке новостей")
    @Test
    public void shouldCreateNews() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = generateWithTimestamp("Created_News_");
        String publishDate = generateDateStringDaysFuture(0);
        String publishTime = generateTimeStringMinutesOffset(5);
        String description = generateWithTimestamp("Test_");
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.assertNewsDoesNotExist(
                title,
                description
        );
        newsCPScreen.clickCreateNewsButton();

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsCreatingForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .save();

        newsCPScreen
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(
                        title,
                        description
                );
    }

    @Story("Отображение элементов экрана")
    @Description("Скроллинг экрана до 8ой новости в списке")
    @Test
    public void shouldScrollToEighthNews() {
        String newsTargetTitle = titlePrefix + 8;
        String newsTargetDescription = descriptionPrefix + 8;

        newsCPScreen.assertCardByTwoTexts(
                newsTargetTitle,
                newsTargetDescription
        );
    }

    @Story("Отображение элементов экрана")
    @Description("Скроллинг экрана до 9ой новости в списке")
    // Тест падает из-за того, что приложение не грузит больше восьми новостей
    @Test
    public void shouldScrollToNinthNews(){
        String newsTargetTitle = titlePrefix + 9;
        String newsTargetDescription = descriptionPrefix + 9;

        newsCPScreen.assertCardByTwoTexts(
                newsTargetTitle,
                newsTargetDescription
        );
    }

    @Story("Создание новости")
    @Description("Отмена создания новости не создаёт новость")
    @Test
    public void shouldCancelFormWithoutCreatingNews() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = generateWithTimestamp("Created_News_");
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(0);
        String description = generateWithTimestamp("Test_");
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.assertNewsDoesNotExist(
                title,
                description
        );
        newsCPScreen.clickCreateNewsButton();

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsCreatingForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .cancel();

        newsCPScreen
                .assertNewsListIsDisplayed()
                .assertNewsDoesNotExist(
                        title,
                        description
                );
    }

    @Story("Редактирование новости")
    @Description("Отмена редактирования новости не применяет изменения к новости")
    @Test
    public void shouldCancelFormWithoutEditingNews() {
        int targetHumanPosition = 1;
        AdminNewsItem firstNews = newsCPScreen.getAdminNewsAtPosition(targetHumanPosition);
        String originalTitle = firstNews.getTitleText();
        String originalDescription = firstNews.getDescriptionText();
        String editedTitle = "Edited_" + originalTitle;
        String editedDescription = "Edited_" + originalDescription;
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.clickNewsEditButtonAtPosition(targetHumanPosition);

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsEditingForm()
                .enterNewsTitle(editedTitle)
                .enterNewsDescription(editedDescription)
                .cancel();

        newsCPScreen
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(
                        originalTitle,
                        originalDescription
                )
                .assertNewsDoesNotExist(
                        editedTitle,
                        editedDescription
                );
    }

    @Story("Создание новости")
    @Description("Удаление пробельных символы в начале и в конце строки заголовка после создания новости")
    @Test
    public void shouldTrimTitleFieldOnNewsCreation() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = generateWithTimestamp("Created_News_");
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(0);
        String description = generateWithTimestamp("Test_");
        String titleWithExtraWhitespace = "\n\n\n   " + title + "   \n\n\n";
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.clickCreateNewsButton();

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsCreatingForm()
                .selectCategory(category)
                .enterNewsTitle(titleWithExtraWhitespace)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .save();

        newsCPScreen
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(
                        title,
                        description
                )
                .assertNewsDoesNotExist(
                        titleWithExtraWhitespace,
                        description
                );
    }

    @Story("Создание новости")
    @Description("Удаление пробельных символы в начале и в конце строки описания после создания новости")
    @Test
    public void shouldTrimDescriptionFieldOnNewsCreation() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = generateWithTimestamp("Created_News_");
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(0);
        String description = generateWithTimestamp("Test_");
        String descriptionWithExtraWhitespace = "\n\n\n   " + description + "   \n\n\n";
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.clickCreateNewsButton();

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsCreatingForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(descriptionWithExtraWhitespace)
                .save();

        newsCPScreen
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(
                        title,
                        description
                )
                .assertNewsDoesNotExist(
                        title,
                        descriptionWithExtraWhitespace
                );
    }

    @Story("Редактирование новости")
    @Description("Удаление пробельных символы в начале и в конце строки заголовка после редактирования новости")
    // Тест падает из-за того, что после редактирования строка заголовока у новости не нормализуется
    @Test
    public void shouldTrimTitleFieldOnNewsEdition() {
        int targetHumanPosition = 1;
        String editedTitle = generateWithTimestamp("Edited_News_");
        String editedDescription = generateWithTimestamp("Test_");
        String titleWithExtraWhitespace = "\n\n\n   " + editedTitle + "   \n\n\n";
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.clickNewsEditButtonAtPosition(targetHumanPosition);

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsEditingForm()
                .enterNewsTitle(titleWithExtraWhitespace)
                .enterNewsDescription(editedDescription)
                .save();

        newsCPScreen
                .assertNewsListIsDisplayed()
                .assertCardByTwoTexts(
                        editedTitle,
                        editedDescription
                )
                .assertNewsDoesNotExist(
                        titleWithExtraWhitespace,
                        editedDescription
                );
    }

    @Story("Редактирование новости")
    @Description("Удаление пробельных символы в начале и в конце строки описания после редактирования новости")
    // Тест падает из-за того, что после редактирования строка описания у новости не нормализуется
    @Test
    public void shouldTrimDescriptionFieldOnNewsEdition() {
        int targetHumanPosition = 1;
        String editedTitle = generateWithTimestamp("Edited_News_");
        String editedDescription = generateWithTimestamp("Test_");
        String descriptionWithExtraWhitespace = "\n\n\n   " + editedDescription + "   \n\n\n";
        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();

        newsCPScreen.clickNewsEditButtonAtPosition(targetHumanPosition);

        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsEditingForm()
                .enterNewsTitle(editedTitle)
                .enterNewsDescription(descriptionWithExtraWhitespace)
                .save();

        newsCPScreen
                .assertNewsListIsDisplayed()
                .openCardAtPosition(newsCPScreen.findPositionByTitle(editedTitle) + 1)
                .assertCardByTwoTexts(
                        editedTitle,
                        editedDescription
                )
                .assertNewsDoesNotExist(
                        editedTitle,
                        descriptionWithExtraWhitespace
                );
    }





//    @Test
//    public void testNewsItemWithAutoDetectedCategory() {
//        int targetHumanPosition = 1;
//        AdminNewsItem item = newsCPScreen.getAdminNewsAtPosition(targetHumanPosition);
//
//        Log.d("EspressoTest", "=== Данные карточки № " + targetHumanPosition + " ===");
//        Log.d("EspressoTest", "Title: " + item.getTitleText());
//        Log.d("EspressoTest", "PublishDate: " + item.getPublishDateText());
//        Log.d("EspressoTest", "Description: " + item.getDescriptionText());
//        Log.d("EspressoTest", "CreateDate: " + item.getCreateDate());
//        Log.d("EspressoTest", "AuthorName: " + item.getAuthorName());
//        Log.d("EspressoTest", "PublishedStatus: " + item.getPublishedStatus());
//        Log.d("EspressoTest", "=========================");
//        Log.d("EspressoTest", "NewsCategoryId: " + item.getNewsCategoryId());
//        Log.d("EspressoTest", "getNewsCategory: " + item.getNewsCategory());
//        Log.d("EspressoTest", "getNewsRuCategory: " + item.getNewsRuCategory());
//        Log.d("EspressoTest", "=========================");
//
//        if (item.getNewsCategory() == null) {
//            System.out.println("INFO: Категория не распознана — иконка не совпала ни с одним из 8 эталонов.");
//        }
//    }

}
