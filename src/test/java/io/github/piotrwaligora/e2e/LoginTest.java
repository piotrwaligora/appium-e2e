package io.github.piotrwaligora.e2e;

import io.github.piotrwaligora.e2e.ui.pages.CatalogPage;
import io.github.piotrwaligora.e2e.ui.pages.LoginPage;
import io.github.piotrwaligora.e2e.ui.pages.NavigationBar;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseTest {

    @Test(description = "User logs in from the menu and the menu then offers to log out")
    public void shouldLogInFromMenu() {
        new CatalogPage(driver).waitUntilLoaded();
        new NavigationBar(driver).openMenu()
                .openLogin()
                .loginAsListedUser();

        new CatalogPage(driver).waitUntilLoaded();
        boolean logOutAvailable = new NavigationBar(driver).openMenu().isLogOutAvailable();

        assertThat(logOutAvailable).as("log out in the menu").isTrue();
    }

    @Test(description = "Login with an empty form is rejected: the username is required")
    public void shouldRequireUsername() {
        new CatalogPage(driver).waitUntilLoaded();
        LoginPage login = new NavigationBar(driver).openMenu().openLogin();

        login.submit();

        assertThat(login.getUsernameError()).as("username error").contains("Username is required");
    }
}
