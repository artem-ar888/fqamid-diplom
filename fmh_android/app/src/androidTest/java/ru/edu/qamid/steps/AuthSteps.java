package ru.edu.qamid.steps;

import ru.edu.qamid.screens.AppBar;
import ru.edu.qamid.screens.AuthScreen;

public class AuthSteps {
    public AuthSteps() {
        this.authScreen = new AuthScreen();
    }

    private final AuthScreen authScreen;

    public AppBar loginStep(String login, String password) {
        authScreen
                .enterLogin(login)
                .enterPassword(password)
                .clickEnter();
        return new AppBar();
    }

    public void logoutStep() {
        new AppBar()
                .clickAuthButton()
                .clickLogout();
    }
}
