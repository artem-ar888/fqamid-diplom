package ru.edu.qamid.screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static ru.edu.qamid.utils.helpers.AllureHelper.reportAllureStep;
import static ru.edu.qamid.utils.helpers.ResourceHelper.getString;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import ru.edu.qamid.R;

public class AuthScreen {

    private static final int ID_APP_BAR = R.id.auth_app_bar;
    private static final int ID_LOGIN_FIELD = R.id.login_edit_text;
    private static final int ID_PASSWORD_FIELD = R.id.password_edit_text;
    private static final int ID_ENTER_BUTTON = R.id.enter_button;
    private static final int ID_LOGIN_LAYOUT = R.id.login_text_input_layout;
    private static final int ID_PASSWORD_LAYOUT = R.id.password_text_input_layout;
    private static final int ID_NAV_HOST_FRAGMENT = R.id.nav_host_fragment;
    private static final String RES_EMPTY_ERROR = getString(R.string.empty_login_or_password);
    private static final String RES_AUTH_STR = getString(R.string.authorization);

    public AuthScreen enterLogin(String login) {
        reportAllureStep("Вводим логин «" + login + "» в поле авторизации", () -> {

            Espresso.onView(ViewMatchers.withId(ID_LOGIN_FIELD))
                    .check(matches(ViewMatchers.isDisplayed()))
                    .perform(ViewActions.replaceText(login));

        });
        return this;
    }

    public AuthScreen enterPassword(String password) {
        reportAllureStep("Вводим пароль «" + password + "» в поле пароля", () -> {

            Espresso.onView(ViewMatchers.withId(ID_PASSWORD_FIELD))
                    .check(matches(ViewMatchers.isDisplayed()))
                    .perform(ViewActions.replaceText(password));

        });
        return this;
    }

    public void clickEnter() {
        reportAllureStep("Нажимаем кнопку «Войти»", () -> {

            Espresso.onView(ViewMatchers.withId(ID_ENTER_BUTTON))
                    .check(matches(ViewMatchers.isDisplayed()))
                    .perform(ViewActions.click());

        });
    }

    public AuthScreen checkEmptyLoginError() {
        reportAllureStep("Проверяем, что отображается ошибка «Логин и пароль не могут быть пустыми» в поле логина", () -> {

            checkErrorInLayout(ID_LOGIN_LAYOUT, RES_EMPTY_ERROR);

        });
        return this;

    }

    public AuthScreen checkEmptyPasswordError() {
        reportAllureStep("Проверяем, что отображается ошибка «Логин и пароль не могут быть пустыми» в поле пароля", () -> {

            checkErrorInLayout(ID_PASSWORD_LAYOUT, RES_EMPTY_ERROR);

        });
        return this;
    }

    private void checkErrorInLayout(int layoutId, String expectedText) {
        onView(allOf(
                withText(expectedText),
                isDescendantOfA(withId(layoutId))
        ))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    public void checkScreenTitle() {
        reportAllureStep("Проверяем, что заголовок экрана содержит текст «Авторизация»", () -> {

            onView(allOf(
                    withText(RES_AUTH_STR),
                    withParent(withParent(withId(ID_NAV_HOST_FRAGMENT)))
            ))
                    .check(matches(isDisplayed()));

        });
    }
}
