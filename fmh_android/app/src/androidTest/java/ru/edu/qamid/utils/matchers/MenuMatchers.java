package ru.edu.qamid.utils.matchers;

import android.view.MenuItem;

import androidx.annotation.IdRes;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public final class MenuMatchers {

    private MenuMatchers() {
    }

    public static Matcher<Object> withMenuItemId(@IdRes final int itemId) {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(Object o) {
                // Проверяем, что объект вообще является MenuItem перед кастом
                if (o instanceof MenuItem) {
                    return ((MenuItem) o).getItemId() == itemId;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with menu item id: ").appendValue(itemId);
            }
        };
    }

    public static Matcher<Object> withMenuItemTitle(final String expectedTitle) {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof MenuItem)) {
                    return false;
                }

                MenuItem menuItem = (MenuItem) o;
                CharSequence title = menuItem.getTitle();

                // Если у пункта нет заголовка — не совпадает
                if (title == null) {
                    return false;
                }

                return expectedTitle.equals(title.toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with menu item title: ").appendValue(expectedTitle);
            }
        };
    }

    public static Matcher<Object> withMenuItemTitleContains(final String expectedSubstring) {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof MenuItem)) {
                    return false;
                }

                MenuItem menuItem = (MenuItem) o;
                CharSequence title = menuItem.getTitle();

                if (title == null) {
                    return false;
                }

                return title.toString().contains(expectedSubstring);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with menu item title containing: ").appendValue(expectedSubstring);
            }
        };
    }

    public static Matcher<Object> withMenuItemEnabled(final boolean expectedEnabled) {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof MenuItem)) {
                    return false;
                }
                MenuItem menuItem = (MenuItem) o;
                return menuItem.isEnabled() == expectedEnabled;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with menu item enabled state: ")
                        .appendValue(expectedEnabled);
            }
        };
    }
}