package ru.edu.qamid.utils.matchers;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.matcher.BoundedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class DebugMatchers {
    public static Matcher<View> logEnabledState(String tag) {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("logEnabledState(" + tag + ")");
            }

            @Override
            protected boolean matchesSafely(View item) {
                boolean isEnabled = item.isEnabled();
                boolean isClickable = item.isClickable();
                Log.d(tag, "Button Enabled: " + isEnabled + ", Clickable: " + isClickable);
                return true; // Всегда true, мы просто логируем
            }
        };
    }
}
