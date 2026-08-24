package ru.edu.qamid.utils.helpers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static ru.edu.qamid.utils.helpers.ConvertHelper.checkAndConvertHumanPositionToZeroBased;
import static ru.edu.qamid.utils.helpers.RecyclerViewHelper.performOnCardAtPosition;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.espresso.PerformException;

import org.hamcrest.Matcher;

import ru.edu.qamid.utils.interfaces.TextCallback;

public class ListActions {

    /**
     * Клик по элементу с указанным ID внутри карточки.
     */
    public static void clickInCardAtPosition(int recyclerViewId, int humanPosition, int viewId) {
        int index = checkAndConvertHumanPositionToZeroBased(humanPosition);

        performOnCardAtPosition(recyclerViewId, index, cardRoot -> {
            View target = cardRoot.findViewById(viewId);
            if (target == null) {
                throw new IllegalStateException("View with ID " + viewId + " not found in card at position " + humanPosition);
            }
            target.performClick();
        });
    }

    /**
     * Проверить, что элемент с указанным ID виден (isShown) внутри карточки.
     */
    public static void checkVisibilityInCardAtPosition(int recyclerViewId, int humanPosition, int viewId) {
        int index = checkAndConvertHumanPositionToZeroBased(humanPosition);

        performOnCardAtPosition(recyclerViewId, index, cardRoot -> {
            View target = cardRoot.findViewById(viewId);
            if (target == null) {
                throw new IllegalStateException("View with ID " + viewId + " not found in card at position " + humanPosition);
            }
            if (!target.isShown()) {
                throw new AssertionError("View with ID " + viewId + " is not visible in the card at position " + humanPosition);
            }
        });
    }

    /**
     * Получить текст из элемента с указанным ID и передать его в callback.
     */
    public static void getTextInCard(int recyclerViewId, int humanPosition, int viewId, @NonNull TextCallback callback) {
        int index = checkAndConvertHumanPositionToZeroBased(humanPosition);

        performOnCardAtPosition(recyclerViewId, humanPosition, cardRoot -> {
            View target = cardRoot.findViewById(viewId);
            if (target == null) {
                throw new IllegalStateException("View with ID " + viewId + " not found in card at position " + humanPosition);
            }

            // Безопасное приведение к TextView
            if (!(target instanceof TextView)) {
                throw new ClassCastException("View with ID " + viewId + " is not a TextView");
            }

            String text = ((TextView) target).getText().toString();
            callback.onTextReady(text);
        });
    }

    // Вспомогательный метод: только создаёт матчер
    private static Matcher<View> getCardMatcher(
            int firstElementId, String firstText,
            int secondElementId, String secondText
    ) {
        return allOf(
                hasDescendant(allOf(withId(firstElementId), withText(firstText))),
                hasDescendant(allOf(withId(secondElementId), withText(secondText)))
        );
    }

    /**
     * Скроллит к карточке и проверяет, что она существует с нужными текстами.
     */
    public static void scrollToAndAssertCardByTwoTexts(
            int recyclerViewId,
            int firstElementWithTextId, String firstText,
            int secondElementWithTextId, String secondText
    ) {
        Matcher<View> cardMatcher = getCardMatcher(firstElementWithTextId, firstText,
                secondElementWithTextId, secondText);

        // Шаг 1: Скроллим к элементу.
        // scrollTo() внутри себя уже делает проверку, что элемент есть в адаптере.
        // Если нет — будет NoMatchingViewException.
        onView(withId(recyclerViewId))
                .perform(scrollTo(cardMatcher));

        // Шаг 2: Жёстко проверяем, что карточка действительно содержит оба текста.
        // Это защищает от ложных срабатываний, если матчер совпал по частичному совпадению.
        onView(withId(recyclerViewId))
                .check(matches(hasDescendant(cardMatcher)));
    }

    /**
     * Находит карточку (по двум текстам) и кликает указанную кнопку внутри неё.
     */
    public static void clickButtonInCard(
            int recyclerViewId,
            int buttonId,
            int firstElementWithTextId, String firstText,
            int secondElementWithTextId, String secondText
    ) {
        // 1. Гарантируем, что карточка найдена и прокручена
        Matcher<View> cardMatcher = getCardMatcher(firstElementWithTextId, firstText,
                secondElementWithTextId, secondText);
        onView(withId(recyclerViewId)).perform(scrollTo(cardMatcher));

        // 2. Строим матчер конкретно для кнопки удаления.
        // Он говорит: "Найди view с ID buttonId, у которого в предках есть карточка с такими текстами"
        Matcher<View> buttonMatcher = allOf(
                withId(buttonId),
                cardMatcher
        );

        // 3. Выполняем клик
        onView(buttonMatcher).perform(click());
    }

    /**
     * ПРОВЕРКА ОТСУТСТВИЯ:
     * Пытается скроллить к карточке.
     * - Если скролл успешен -> карточка ЕСТЬ (тест падает: баг, не удалили).
     * - Если скролл падает (NoMatchingViewException) -> карточки НЕТ (тест проходит).
     */
    public static void assertCardDoesNotExist(
            int recyclerViewId,
            int firstElementWithTextId, String firstText,
            int secondElementWithTextId, String secondText
    ) {
        Matcher<View> cardMatcher = getCardMatcher(firstElementWithTextId, firstText,
                secondElementWithTextId, secondText);
        try {
            // Пытаемся скроллить к карточке.
            onView(withId(recyclerViewId)).perform(scrollTo(cardMatcher));

            // Если мы здесь — scrollTo успешно нашёл карточку. Это FAIL.
            throw new AssertionError(
                    "Card with title '" + firstText + "' still exists in the list!"
            );
        } catch (PerformException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                String msg = cause.getMessage();
                if (msg != null && msg.contains("Found 0 items")) {
                    // Скролл прошёл весь список, карточки нет.
                    // Тест успешен.
                    return;
                }
            }
            // Если это какая-то другая ошибка (не «Found 0 items»), пробрасываем дальше.
            throw e;
        }
    }

}
