package ru.edu.qamid.utils.helpers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ru.edu.qamid.utils.helpers.ConvertHelper.checkAndConvertHumanPositionToZeroBased;
import static ru.edu.qamid.utils.matchers.IconMatcher.detectCategoryFromBitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import ru.edu.qamid.utils.data.AdminNewsItem;
import ru.edu.qamid.utils.data.ItemType;
import ru.edu.qamid.utils.data.NewsItem;
import ru.edu.qamid.utils.data.QuoteItem;
import ru.edu.qamid.utils.interfaces.CardAction;

public class RecyclerViewHelper {

    /**
     * Получаем карточку из списка
     */
    public static void performOnCardAtPosition(
            int recyclerViewId,
            int position,
            CardAction action
    ) {
        onView(withId(recyclerViewId))
                .perform(
                        scrollToPosition(position),
                        actionOnItemAtPosition(
                                position,
                                new ViewAction() {
                                    @Override
                                    public Matcher<View> getConstraints() {
                                        return null;
                                    }

                                    @Override
                                    public String getDescription() {
                                        return "Perform action on card at position " + position;
                                    }

                                    @Override
                                    public void perform(UiController uiController, View view) {
                                        // view — это и есть cardRoot (корневой View карточки)
                                        action.perform(view);
                                    }
                                }
                        )
                );
    }


    /**
     * Безопасно кликает по кнопке внутри элемента списка.
     */
    public static void clickViewAtPositionSafe(int recyclerViewId, int position, int viewId) {
        onView(withId(recyclerViewId))
                .perform(
                        scrollToPosition(position),
                        actionOnItemAtPosition(
                                position,
                                new RecyclerViewActions.PositionableRecyclerViewAction() {
                                    @Override
                                    public RecyclerViewActions.PositionableRecyclerViewAction atPosition(int pos) {
                                        return this;
                                    }

                                    @Override
                                    public void perform(UiController uiController, View view) {
                                        View btn = view.findViewById(viewId);
                                        if (btn == null) {
                                            throw new IllegalStateException(
                                                    "View with id " + viewId + " not found inside element at position " + position
                                            );
                                        }
                                        btn.performClick();
                                    }

                                    @Override
                                    public org.hamcrest.Matcher<View> getConstraints() {
                                        return null;
                                    }

                                    @Override
                                    public String getDescription() {
                                        return "Click view id " + viewId + " at position " + position;
                                    }
                                }
                        )
                );
    }

    // Диспетчер для Цитат (только те параметры, которые нужны цитатам)
    public static QuoteItem getItemAtPosition(ItemType type, int recyclerViewId, int position,
                                              int idTitle, int idDescription) {
        if (type != ItemType.QUOTE) {
            throw new IllegalArgumentException("This method is only for quotes items. Received type: " + type);
        }
        return getQuoteItemAtPosition(recyclerViewId, position, idTitle, idDescription);
    }

    // Диспетчер для Новостей (только те параметры, которые нужны новостям)
    public static NewsItem getItemAtPosition(ItemType type, int recyclerViewId, int position,
                                             int idTitle, int idDescription,
                                             int idPublishDate, int idNewsCategory) {
        if (type != ItemType.NEWS) {
            throw new IllegalArgumentException("This method is only for news items. Received type: " + type);
        }
        return getNewsItemAtPosition(recyclerViewId, position, idTitle, idDescription, idPublishDate, idNewsCategory);
    }

    // Диспетчер для Расширенных Новостей (только те параметры, которые нужны расширенным новостям)
    public static AdminNewsItem getItemAtPosition(ItemType type, int recyclerViewId, int position,
                                                  int idTitle, int idDescription,
                                                  int idPublishDate, int idNewsCategory, int idCreateDate,
                                                  int idAuthorName, int idPublishedStatus) {
        if (type != ItemType.ADMIN_NEWS) {
            throw new IllegalArgumentException("This method is only for extended news items. Received type: " + type);
        }
        return getAdminNewsItemAtPosition(recyclerViewId, position, idTitle, idDescription, idPublishDate,
                idNewsCategory, idCreateDate, idAuthorName, idPublishedStatus);
    }

    public static QuoteItem getQuoteItemAtPosition(int recyclerViewId, int position,
                                                   int idTitle, int idDescription) {
        final QuoteItem item = new QuoteItem();

        onView(withId(recyclerViewId))
                .perform(actionOnItemAtPosition(position, new RecyclerViewActions.PositionableRecyclerViewAction() {
                    @Override
                    public void perform(UiController uiController, View view) {
                        item.setTitleText(((TextView) view.findViewById(idTitle)).getText().toString());
                        item.setDescriptionText(((TextView) view.findViewById(idDescription)).getText().toString());
                    }

                    @Override
                    public RecyclerViewActions.PositionableRecyclerViewAction atPosition(int pos) {
                        return this;
                    }

                    @Override
                    public org.hamcrest.Matcher<View> getConstraints() {
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Get QuoteItem at pos " + position;
                    }
                }));

        return item;
    }

    public static NewsItem getNewsItemAtPosition(int recyclerViewId, int position,
                                                 int idTitle, int idDescription,
                                                 int idPublishDate, int idNewsCategory) {
        final NewsItem item = new NewsItem();

        onView(withId(recyclerViewId))
                .perform(actionOnItemAtPosition(position, new RecyclerViewActions.PositionableRecyclerViewAction() {

                    @Override
                    public void perform(UiController uiController, View view) {
                        item.setTitleText(getText(view, idTitle));
                        item.setDescriptionText(getText(view, idDescription));
                        item.setPublishDateText(getText(view, idPublishDate));
                        item.setNewsCategory(getText(view, idNewsCategory));
                    }

                    private String getText(View v, int id) {
                        TextView t = v.findViewById(id);
                        return t != null ? t.getText().toString() : null;
                    }

                    @Override
                    public RecyclerViewActions.PositionableRecyclerViewAction atPosition(int pos) {
                        return this;
                    }

                    @Override
                    public org.hamcrest.Matcher<View> getConstraints() {
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Get NewsItem at pos " + position;
                    }
                }));

        return item;
    }

    public static AdminNewsItem getAdminNewsItemAtPosition(int recyclerViewId, int position,
                                                           int idTitle, int idDescription,
                                                           int idPublishDate, int idNewsCategory,
                                                           int idCreateDate, int idAuthorName,
                                                           int idPublishedStatus) {
        // AtomicReference нужен, чтобы передать объект из perform (асинхронно) в возвращаемое значение метода
        final AtomicReference<AdminNewsItem> resultRef = new AtomicReference<>();

        onView(withId(recyclerViewId))
                .perform(actionOnItemAtPosition(position, new RecyclerViewActions.PositionableRecyclerViewAction() {
                    @Override
                    public void perform(UiController uiController, View view) {
                        // 1. Ждём минимум 1 сек, чтобы картинка успела отрисоваться (особенно при асинхронной загрузке).
                        // Это правильный способ ожидания в Espresso (не Thread.sleep!).
                        uiController.loopMainThreadForAtLeast(1000);

                        // 2. Создаём объект прямо здесь, внутри perform
                        AdminNewsItem item = new AdminNewsItem();

                        item.setTitleText(getText(view, idTitle));
                        item.setDescriptionText(getText(view, idDescription));
                        item.setPublishDateText(getText(view, idPublishDate));
                        item.setCreateDate(getText(view, idCreateDate));
                        item.setAuthorName(getText(view, idAuthorName));
                        item.setPublishedStatus(getText(view, idPublishedStatus));

                        // 3. Получаем Bitmap из иконки и детектим категорию
                        String detectedCategory = null;
                        ImageView iconView = view.findViewById(idNewsCategory);
                        if (iconView != null) {
                            Drawable drawable = iconView.getDrawable();
                            if (drawable instanceof BitmapDrawable) {
                                Bitmap iconBitmap = ((BitmapDrawable) drawable).getBitmap();
                                // Вызываем нашу утилиту детекции
                                detectedCategory = detectCategoryFromBitmap(iconBitmap);
                            } else {
                                System.out.println("WARN: ImageView does not contain BitmapDrawable. Type: " + drawable.getClass().getSimpleName());
                            }
                        } else {
                            System.out.println("WARN: ImageView with ID " + idNewsCategory + " not found in the element.");
                        }

                        // Присваиваем категорию (может быть null, если не совпала ни с одной)
                        item.setNewsCategory(detectedCategory);

                        // Сохраняем в AtomicReference, чтобы вернуть наружу
                        resultRef.set(item);
                    }

                    private String getText(View v, int id) {
                        TextView t = v.findViewById(id);
                        return (t != null && t.getText() != null) ? t.getText().toString() : null;
                    }

                    @Override
                    public RecyclerViewActions.PositionableRecyclerViewAction atPosition(int pos) {
                        return this;
                    }

                    @Override
                    public org.hamcrest.Matcher<View> getConstraints() {
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Get AdminNewsItem at pos " + position;
                    }
                }));

        // Возвращаем объект, который был создан и заполнен внутри perform
        return resultRef.get();
    }

    public static void openItemAtPositionWithConvertPosition(int recyclerViewId, int humanPosition, int cardViewId) {
        int index = checkAndConvertHumanPositionToZeroBased(humanPosition);
        clickViewAtPositionSafe(recyclerViewId, index, cardViewId);
    }

    public static int findPositionByText(int recyclerViewId, int textViewId, String expectedText) {
        Activity activity = getCurrentActivity();
        RecyclerView rv = activity.findViewById(recyclerViewId);

        if (rv.getAdapter() == null) return -1;

        for (int i = 0; i < rv.getAdapter().getItemCount(); i++) {
            RecyclerView.ViewHolder holder = rv.findViewHolderForAdapterPosition(i);
            if (holder == null || holder.itemView == null) continue;

            View tv = holder.itemView.findViewById(textViewId);
            if (tv instanceof android.widget.TextView) {
                if (((android.widget.TextView) tv).getText().toString().equals(expectedText)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static Activity getCurrentActivity() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        final Activity[] holder = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED);
            if (!activities.isEmpty()) {
                holder[0] = activities.iterator().next();
            }
        });
        if (holder[0] == null) {
            throw new RuntimeException("Activity not found. Make sure the test is running.");
        }
        return holder[0];
    }


}