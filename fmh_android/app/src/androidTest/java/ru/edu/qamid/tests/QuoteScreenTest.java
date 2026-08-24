package ru.edu.qamid.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Before;
import org.junit.Test;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.Story;
import ru.edu.qamid.screens.QuoteScreen;
import ru.edu.qamid.steps.AuthSteps;
import ru.edu.qamid.utils.data.QuoteItem;
import ru.edu.qamid.utils.data.TestData;

@Feature("Экран «Цитаты»")
public class QuoteScreenTest extends BaseE2eEspressoTest {
    private QuoteScreen quoteScreen;

    @Before
    public void setup() {
        AuthSteps authSteps = new AuthSteps();
        authSteps.loginStep(TestData.LOGIN, TestData.PASSWORD);
        quoteScreen = onQuoteScreen();
    }

    @Story("Отображение списка с цитатами")
    @Description("Список с карточками цитат отображается")
    @Test
    public void shouldBeDisplayedQuoteList() {
        quoteScreen.assertQuoteListIsDisplayed();
    }

    @Story("Работа кнопки цитаты в AppBar")
    @Description("Кнопка цитат не открывает экран заново")
    @Test
    public void appBarButtonDoesNotResetListState() {
        int targetHumanPosition = 8; // Целимся в 8-й элемент

        quoteScreen.openCardAtPosition(targetHumanPosition);
        QuoteItem item = quoteScreen.getQuoteAtPosition(targetHumanPosition);
        String descriptionText = item.getDescriptionText();

        quoteScreen.clickQuoteButton();

        onView(withText(descriptionText)).check(matches(isDisplayed()));
    }
}
