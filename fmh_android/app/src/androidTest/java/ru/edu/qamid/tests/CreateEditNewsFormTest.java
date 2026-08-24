package ru.edu.qamid.tests;

import static ru.edu.qamid.utils.data.DataGenerator.generateDateStringDaysAgo;
import static ru.edu.qamid.utils.data.DataGenerator.generateDateStringDaysFuture;
import static ru.edu.qamid.utils.data.DataGenerator.generateTimeStringMinutesOffset;
import static ru.edu.qamid.utils.data.DataGenerator.generateWithTimestamp;
import static ru.edu.qamid.utils.data.DataGenerator.getCategoryName;
import static ru.edu.qamid.utils.data.DataGenerator.randomIntBetween;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.Story;
import ru.edu.qamid.screens.CreateEditNewsForm;
import ru.edu.qamid.screens.NewsCPScreen;
import ru.edu.qamid.steps.ApiSteps;
import ru.edu.qamid.steps.AuthSteps;
import ru.edu.qamid.utils.data.DataGenerator;
import ru.edu.qamid.utils.data.TestData;

@Feature("Форма «Создание/Редактирование Новости»")
public class CreateEditNewsFormTest extends BaseE2eEspressoTest{

    private ApiSteps apiSteps;
    private NewsCPScreen newsCPScreen;
    private String titlePrefix;
    private String descriptionPrefix;

    @Before
    public void setup() throws IOException {
        int countOfNews = 1;
        titlePrefix = DataGenerator.generateWithTimestamp("Test_title_", "_");
        descriptionPrefix = DataGenerator.generateWithTimestamp("Test_description_", "_");

        apiSteps = prepareIsolatedNewsData();

        AuthSteps authSteps = new AuthSteps();
        authSteps.loginStep(TestData.LOGIN, TestData.PASSWORD);
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

    /**
     * Фабрика: возвращает готовый CreateEditNewsForm для создания новости
     */
    private CreateEditNewsForm startFromCreateNewsForm() {
        newsCPScreen.assertNewsCPScreenIsDisplayed();
        newsCPScreen.clickCreateNewsButton();

        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();
        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsCreatingForm();
        return createEditNewsForm;
    }

    /**
     * Фабрика: возвращает готовый CreateEditNewsForm для редактирования 1ой новости
     */
    private CreateEditNewsForm startFromEditNewsForm() {
        int targetHumanPosition = 1;

        newsCPScreen.assertNewsCPScreenIsDisplayed();
        newsCPScreen.clickNewsEditButtonAtPosition(targetHumanPosition);

        CreateEditNewsForm createEditNewsForm = new CreateEditNewsForm();
        createEditNewsForm
                .assertCreateEditNewsFormIsDisplayed()
                .assertIsEditingForm();
        return createEditNewsForm;
    }

    @Story("Успешное сохранение новости")
    @Description("Содание новости прошло успешно, в результате открывается экран «Панель управления»")
    @Test
    public void shouldSavingCreatingNewsAndShowsNewsCPScreen() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = category + "_test";
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(2);
        String description = generateWithTimestamp("Test_");

        startFromCreateNewsForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .assertSwitchEnabledState(false)
                .assertSwitchCheckedState(true)
                .save();

        newsCPScreen.assertNewsCPScreenIsDisplayed();
    }

    @Story("Ошибки сохранения новости")
    @Description("Содание новости с пустыми полями невозможно, в незаполненных полях отображаются ошибки")
    @Test
    public void shouldNotSavingEmptyForm() {
        startFromCreateNewsForm()
                .save()
                .assertCategoryHasErrorIcon()
                .assertTitleHasErrorIcon()
                .assertPublishDateHasErrorIcon()
                .assertPublishTimeHasErrorIcon()
                .assertDescriptionHasErrorIcon()
                .assertCreateEditNewsFormIsDisplayed();

    }

    @Story("Работа элементов формы")
    @Description("Отмена создания новости, форма закрывается и происходит переход на экран «Панель управления»")
    @Test
    public void shouldNotSavingCreatingNewsAndShowsNewsCPScreen() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = category + "_test";
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(2);
        String description = generateWithTimestamp("Test_");

        startFromCreateNewsForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .assertSwitchEnabledState(false)
                .assertSwitchCheckedState(true)
                .cancel();

        newsCPScreen.assertNewsCPScreenIsDisplayed();
    }

    @Story("Работа элементов формы")
    @Description("Отмена отмены создания новости, отображается форма «Создание новости»")
    @Test
    public void shouldCancelCancelsAndStaysOnForm() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = category + "_test";
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(2);
        String description = generateWithTimestamp("Test_");

        startFromCreateNewsForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .assertSwitchEnabledState(false)
                .assertSwitchCheckedState(true)
                .clickCancelButton()
                .verifyCancelConfirmationDialog()
                .cancelCancel()
                .assertCreateEditNewsFormIsDisplayed();
    }

    @Story("Успешное сохранение новости")
    @Description("Редактирование новости прошло успешно, в результате открывается экран «Панель управления»")
    @Test
    public void shouldSavingEditingNewsAndShowsNewsCPScreen() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = category + "_test";
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(2);
        String description = generateWithTimestamp("Test_");

        startFromEditNewsForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .assertSwitchEnabledState(true)
//                .assertSwitchCheckedState(false)
                .ensureSwitchActiveState(false)
                .save();

        newsCPScreen.assertNewsCPScreenIsDisplayed();
    }

    @Story("Работа элементов формы")
    @Description("Отмена редактирования новости, форма закрывается и происходит переход на экран «Панель управления»")
    @Test
    public void shouldNotSavingEditingNewsAndShowsNewsCPScreen() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = category + "_test";
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(2);
        String description = generateWithTimestamp("Test_");

        startFromEditNewsForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .assertSwitchEnabledState(true)
                .ensureSwitchActiveState(true)
                .cancel();

        newsCPScreen.assertNewsCPScreenIsDisplayed();
    }

    @Story("Работа элементов формы")
    @Description("Переключение у создаваемой новости тумблера на «Не активна," +
            " форма сохраняется и происходит переход на экран «Панель управления»")
    // Тест падает из-за того, что тумблер неактивен
    @Test
    public void shouldCreateInactiveNews() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = category + "_test";
        String publishDate = generateDateStringDaysFuture(1);
        String publishTime = generateTimeStringMinutesOffset(2);
        String description = generateWithTimestamp("Test_");

        startFromCreateNewsForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .assertSwitchEnabledState(true)
                .ensureSwitchActiveState(false)
                .save();
        newsCPScreen.assertNewsCPScreenIsDisplayed();
    }

    @Story("Работа элементов формы")
    @Description("Переключение у редактируемой новости тумблера на «Не активна," +
            " форма сохраняется и происходит переход на экран «Панель управления»")
    @Test
    public void shouldSavingEditingInactiveNews() {
        startFromEditNewsForm()
                .assertSwitchEnabledState(true)
                .ensureSwitchActiveState(false)
                .save();
        newsCPScreen.assertNewsCPScreenIsDisplayed();
    }

    @Story("Ошибки сохранения новости")
    @Description("Содание новости с прошедшей датой публикации невозможно, отображается форма «Создание новости»")
    // Тест падает из-за того, что приложение не запрещает создание новости с прошедшим временем
    @Test
    public void shouldNotCreateNewsWithPastDateAndStayOnForm() {
        String category = getCategoryName(randomIntBetween(1, 8));
        String title = category + "_test";
        String publishDate = generateDateStringDaysAgo(1);
        String publishTime = generateTimeStringMinutesOffset(0);
        String description = generateWithTimestamp("Test_");

        startFromCreateNewsForm()
                .selectCategory(category)
                .enterNewsTitle(title)
                .enterNewsPublishDate(publishDate)
                .enterNewsPublishTime(publishTime)
                .enterNewsDescription(description)
                .save()
                .assertCreateEditNewsFormIsDisplayed();
    }
}
