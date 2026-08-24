package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;
import static ru.edu.qamid.utils.helpers.ConvertHelper.checkAndConvertHumanPositionToZeroBased;
import static ru.edu.qamid.utils.helpers.RecyclerViewHelper.openItemAtPositionWithConvertPosition;
import static ru.edu.qamid.utils.helpers.ResourceHelper.getString;

import ru.edu.qamid.R;
import ru.edu.qamid.utils.data.ItemType;
import ru.edu.qamid.utils.data.QuoteItem;
import ru.edu.qamid.utils.helpers.RecyclerViewHelper;


public class QuoteScreen extends AppBar {
    public QuoteScreen() {
    }

    private static final int ID_QUOTE_SCREEN_TITLE = R.id.our_mission_title_text_view;
    private static final String RES_QUOTE_TITLE_STR = getString(R.string.our_mission_title_text);
    private static final int ID_LIST = R.id.our_mission_item_list_recycler_view;
    private static final int ID_QUOTE_EL_TITLE = R.id.our_mission_item_title_text_view;
    private static final int ID_QUOTE_EL_DESC = R.id.our_mission_item_description_text_view;
    private static final int ID_QUOTE_EL_EXPAND_BTN = R.id.our_mission_item_open_card_image_button;
    private static final int ID_MATERIAL_CARD = R.id.our_mission_item_material_card_view;

    public void checkQuoteScreenTitle() {
        reportAllureStep("Проверяем, что заголовок экрана цитат отображается", () -> {

            onView(allOf(
                    withId(ID_QUOTE_SCREEN_TITLE),
                    withText(RES_QUOTE_TITLE_STR)
            ))
                    .check(matches(isDisplayed()));

        });
    }

    public QuoteScreen assertQuoteListIsDisplayed() {
        reportAllureStep("Проверяем, что список цитат (RecyclerView) отображается на экране", () -> {

            onView(withId(ID_LIST))
                    .check(matches(isDisplayed()));

        });
        return this;
    }

    /**
     * Получает данные из карточки. Работает для любых позиций.
     */
    public QuoteItem getQuoteAtPosition(int humanPosition) {
        int index = checkAndConvertHumanPositionToZeroBased(humanPosition);
        return RecyclerViewHelper.getItemAtPosition(
                ItemType.QUOTE,
                ID_LIST,
                index,
                ID_QUOTE_EL_TITLE,
                ID_QUOTE_EL_DESC
        );
    }

    public void openCardAtPosition(int humanPosition) {
        reportAllureStep("Открываем карточку цитаты на позиции " + humanPosition, () -> {

            openItemAtPositionWithConvertPosition(ID_LIST, humanPosition, ID_MATERIAL_CARD);

        });
    }

    public void closeCardAtPosition(int humanPosition) {
        reportAllureStep("Закрываем карточку цитаты на позиции " + humanPosition +
                " (повторным нажатием)", () -> {

            openCardAtPosition(humanPosition);

        });
    }
}

