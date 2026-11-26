package io.github.piotrwaligora.e2e;

import io.github.piotrwaligora.e2e.ui.pages.CatalogPage;
import io.github.piotrwaligora.e2e.ui.pages.NavigationBar;
import io.github.piotrwaligora.e2e.ui.pages.ReviewOrderPage;
import io.github.piotrwaligora.e2e.ui.pages.ShippingAddressPage;
import org.testng.annotations.Test;

import static io.github.piotrwaligora.e2e.data.TestData.ADDRESS;
import static io.github.piotrwaligora.e2e.data.TestData.BACKPACK;
import static io.github.piotrwaligora.e2e.data.TestData.CARD;
import static io.github.piotrwaligora.e2e.data.TestData.STANDARD_USER;
import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutTest extends BaseTest {

    @Test(description = "Guest adds a product, logs in at checkout and places the order")
    public void shouldPlaceOrder() {
        new CatalogPage(driver).waitUntilLoaded()
                .openProduct(BACKPACK)
                .addToCart();
        new NavigationBar(driver).openCart()
                .proceedToCheckoutAsGuest()
                .login(STANDARD_USER.username(), STANDARD_USER.password());

        ReviewOrderPage review = new ShippingAddressPage(driver).waitUntilLoaded()
                .enterAddress(ADDRESS)
                .enterCard(CARD);

        // Backpack $29.99 + delivery $5.99
        assertThat(review.getTotal()).as("order total").isEqualTo("$ 35.98");
        assertThat(review.placeOrder().getTitle()).as("confirmation").isEqualTo("Checkout Complete");
    }
}
