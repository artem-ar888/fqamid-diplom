package ru.edu.qamid.utils.helpers;

import androidx.navigation.Navigation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import ru.edu.qamid.R;
import ru.edu.qamid.ui.AppActivity;

public class NavigationHelper {
    private static final int NAV_HOST_FRAGMENT = R.id.nav_host_fragment;
    private static final int AUTH_FRAGMENT = R.id.authFragment;

    public static void forceNavigateToAuth(ActivityScenarioRule<AppActivity> rule) {
        rule.getScenario().onActivity(activity -> {
            var navController = Navigation.findNavController(activity, NAV_HOST_FRAGMENT);
            navController.navigate(AUTH_FRAGMENT);
        });
    }
}