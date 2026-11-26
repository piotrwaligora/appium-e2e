package io.github.piotrwaligora.e2e;

import io.github.piotrwaligora.e2e.ui.pages.CatalogPage;
import io.github.piotrwaligora.e2e.ui.pages.LoginPage;
import io.github.piotrwaligora.e2e.ui.pages.NavigationBar;
import org.testng.annotations.Test;

import static io.github.piotrwaligora.e2e.data.TestData.STANDARD_USER;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseTest {

    @Test(description = "User logs in from the menu and the menu then offers to log out")
    public void shouldLogInFromMenu() {
        new CatalogPage(driver).waitUntilLoaded();
        new NavigationBar(driver).openMenu()
                .openLogin()
                .login(STANDARD_USER.username(), STANDARD_USER.password());

        new CatalogPage(driver).waitUntilLoaded();
        boolean logOutAvailable = new NavigationBar(driver).openMenu().isLogOutAvailable();

        assertThat(logOutAvailable).as("log out in the menu").isTrue();
    }

    @Test(description = "Login without a username is rejected with a message")
    public void shouldRequireUsername() {
        new CatalogPage(driver).waitUntilLoaded();
        LoginPage login = new NavigationBar(driver).openMenu().openLogin();

        login.login("", STANDARD_USER.password());

        assertThat(login.getUsernameError()).as("username error").contains("Username is required");
    }
}
