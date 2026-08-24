package ru.edu.qamid.tests;

import static ru.edu.qamid.utils.matchers.IconMatcher.initCategoryMap;

import android.util.Log;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.Epic;
import ru.edu.qamid.screens.MainScreen;
import ru.edu.qamid.screens.NewsCPScreen;
import ru.edu.qamid.screens.NewsScreen;
import ru.edu.qamid.screens.QuoteScreen;
import ru.edu.qamid.steps.ApiSteps;
import ru.edu.qamid.ui.AppActivity;
import ru.edu.qamid.utils.EspressoIdlingResource;
import ru.edu.qamid.utils.TestMode;
import ru.edu.qamid.utils.helpers.ApiHelper;
import ru.edu.qamid.utils.helpers.NavigationHelper;

@RunWith(AllureAndroidJUnit4.class)

@Epic("E2E тестирование")
public abstract class BaseE2eEspressoTest {

    // ТОЛЬКО для разовой подготовки базы данных
    static {
        System.out.println("[BASE] Running global data preparation...");
        try {
            // Создаём минимально необходимый ApiHelper только для очистки
            ApiHelper tempClient = new ApiHelper();

            // Передаём его в конструктор ApiSteps
            new ApiSteps(tempClient).clearAndFillPageZeroWithStubs();

            System.out.println("[BASE] Global data preparation completed successfully.");
        } catch (Exception e) {
            System.err.println("[BASE] Global data preparation FAILED: " + e.getMessage());
        }
    }

    @Rule
    public ActivityScenarioRule<AppActivity> mActivityScenarioRule = new ActivityScenarioRule<>(AppActivity.class);

    private androidx.test.espresso.idling.CountingIdlingResource idlingResource;

    @Before
    public void baseSetup() {
        TestMode.start();
        initCategoryMap(InstrumentationRegistry.getInstrumentation().getTargetContext());

        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("settings put global window_animation_scale 0");
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("settings put global transition_animation_scale 0");
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("settings put global animator_duration_scale 0");

        idlingResource = EspressoIdlingResource.INSTANCE.getCountingIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);

        NavigationHelper.forceNavigateToAuth(mActivityScenarioRule);
    }

    @After
    public void baseTeardown() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
            idlingResource = null;
        }

        TestMode.stop();
    }

    /**
     * Явная подготовка данных для теста: очистка + создание заглушек.
     * Вызывай ТОЛЬКО в тех тестах, где нужна изоляция данных.
     */
    protected ApiSteps prepareIsolatedNewsData() throws IOException {
        Log.i("E2E_SETUP", "[SETUP] Preparing isolated news data via API...");

        ApiHelper apiClient = new ApiHelper();
        ApiSteps apiSteps = new ApiSteps(apiClient);
        apiSteps.clearLast10DaysAndFillPageZeroWithStubs();

        Log.i("E2E_SETUP", "[SETUP] Test data prepared successfully.");
        return  apiSteps;
    }

    /**
     * Универсальный метод для очистки данных, если у теста есть ApiSteps.
     * Вызывай его в @After конкретного теста, где steps используется.
     */
    protected void safeCleanupSteps(ApiSteps apiSteps) {
        if (apiSteps == null) {
            Log.i("E2E_SETUP", "[TEARDOWN] Skipping cleanup: steps is null");
            return;
        }

        try {
            Log.i("E2E_SETUP", "[TEARDOWN] Running cleanupCreatedNews()...");
            apiSteps.cleanupCreatedNews();
            Log.i("E2E_SETUP", "[TEARDOWN] Cleanup completed successfully.");
        } catch (Exception e) {
            Log.e("E2E_SETUP", "[TEARDOWN] Cleanup failed: " + e.getMessage(), e);
            // Не пробрасываем исключение: пусть упадёт сам тест, а не очистка
        }
    }

    /**
     * Фабрика: возвращает объект MainScreen.
     * Она НЕ делает навигацию, потому что она уже сделана в @Before.
     */
    protected MainScreen onMainScreen() {
        return new MainScreen();
    }

    /**
     * Фабрика: возвращает готовый QuoteScreen (переход + проверка).
     */
    protected QuoteScreen onQuoteScreen() {
        QuoteScreen quoteScreen = new QuoteScreen();

        onMainScreen()
                .clickQuoteButton();
        quoteScreen.checkQuoteScreenTitle();

        return quoteScreen;
    }

    /**
     * Фабрика: возвращает готовый NewsScreen (переход + проверка).
     */
    protected NewsScreen onNewsScreen() {
        NewsScreen newsScreen = new NewsScreen();

        onMainScreen()
                .clickMainMenuButton()
                .clickNews();

        newsScreen.assertNewsScreenIsDisplayed();

        return newsScreen;
    }

    /**
     * Фабрика: возвращает готовый NewsCPScreen (переход + проверка).
     */
    protected NewsCPScreen onNewsCPScreen() {
        NewsCPScreen newsCPScreen = new NewsCPScreen();

        onNewsScreen()
                .clickNewsEditButton();

        newsCPScreen.assertNewsCPScreenIsDisplayed();

        return newsCPScreen;
    }
}
