package ru.edu.qamid.tests;

import org.junit.Before;
import org.junit.Test;

import io.qameta.allure.kotlin.Description;
import io.qameta.allure.kotlin.Feature;
import io.qameta.allure.kotlin.Story;
import ru.edu.qamid.screens.AuthScreen;
import ru.edu.qamid.screens.MainScreen;
import ru.edu.qamid.utils.data.DataGenerator;
import ru.edu.qamid.utils.data.TestData;

@Feature("Экран «Авторизация»")
public class AuthScreenTest extends BaseE2eEspressoTest {

    private AuthScreen authScreen;

    @Before
    public void setup() {
        authScreen = new AuthScreen();
    }

    @Story("Успешная авторизация")
    @Description("Вход существующего пользователя с валидными учётными данными")
    @Test
    public void loginWithValidCredentials() {
        authScreen
                .enterLogin(TestData.LOGIN)
                .enterPassword(TestData.PASSWORD)
                .clickEnter();

        new MainScreen().assertMainScreenIsDisplayed();
    }

    @Story("Ошибки авторизации")
    @Description("Вход отклонён из‑за ввода имени несуществующего пользователя")
    @Test
    public void shouldNotAuthorizeWithFakeUser() {
        String userName = DataGenerator.generateWithTimestamp(TestData.USER_LOGIN_PREFIX);

        authScreen
                .enterLogin(userName)
                .enterPassword(TestData.PASSWORD)
                .clickEnter();

        authScreen.checkScreenTitle();
    }

    @Story("Ошибки авторизации")
    @Description("Вход отклонён из‑за ввода неверного пароля для существующего пользователя")
    @Test
    public void shouldNotAuthorizeWithWrongPassword() {
        authScreen
                .enterLogin(TestData.LOGIN)
                .enterPassword(TestData.WRONG_PASSWORD)
                .clickEnter();

        authScreen.checkScreenTitle();
    }

    @Story("Ошибки авторизации")
    @Description("Вход отклонён из‑за отправки пустой формы")
    @Test
    public void loginWithEmptyFields() {
        authScreen.clickEnter();

        authScreen.checkScreenTitle();
    }

    @Story("Ошибки авторизации")
    @Description("Вход отклонён из‑за пустого логина")
    @Test
    public void loginWithEmptyLoginField() {
        authScreen
                .enterPassword(TestData.PASSWORD)
                .clickEnter();

        authScreen.checkScreenTitle();
    }

    @Story("Ошибки авторизации")
    @Description("Вход отклонён из‑за пустого пароля")
    @Test
    public void loginWithEmptyPasswordField() {
        authScreen
                .enterLogin(TestData.LOGIN)
                .clickEnter();

        authScreen.checkScreenTitle();
    }

    @Story("Ошибки авторизации")
    @Description("Отображение ошибок при отправке пустой формы")
    @Test
    public void emptyFieldsShowErrors() {
        authScreen.clickEnter();

        authScreen
                .checkEmptyLoginError()
                .checkEmptyPasswordError();
    }

    @Story("Ошибки авторизации")
    @Description("Корректная обработка спецсимволов при отправке формы с несуществующим пользователем")
    @Test
    public void loginActivityShouldSurviveSpecialCharInjection() {
        String usrWithSpecials = DataGenerator.randomWithSpecialChars(10);
        String pwdWithSpecials = DataGenerator.randomWithSpecialChars(10);

        authScreen
                .enterLogin(usrWithSpecials)
                .enterPassword(pwdWithSpecials)
                .clickEnter();

        authScreen.checkScreenTitle();
    }

    @Story("Успешная авторизация")
    @Description("Вход возможен с валидными данными, даже если в логине и пароле присутствуют пробелы по краям")
    @Test
    public void shouldLoginWithTrimmedCredentials() {
        authScreen
                .enterLogin(TestData.loginWithBothSpaces())
                .enterPassword(TestData.passwordWithBothSpaces())
                .clickEnter();

        new MainScreen().assertMainScreenIsDisplayed();
    }
}
