package io.github.piotrwaligora.e2e;

import io.github.piotrwaligora.e2e.ui.pages.CartPage;
import io.github.piotrwaligora.e2e.ui.pages.CatalogPage;
import io.github.piotrwaligora.e2e.ui.pages.NavigationBar;
import org.testng.annotations.Test;

import static io.github.piotrwaligora.e2e.data.TestData.BACKPACK;
import static org.assertj.core.api.Assertions.assertThat;

public class CartTest extends BaseTest {

    @Test(description = "Product added from its details is counted in the header and listed in the cart")
    public void shouldAddProductToCart() {
        new CatalogPage(driver).waitUntilLoaded()
                .openProduct(BACKPACK)
                .addToCart();
        NavigationBar navigation = new NavigationBar(driver);

        assertThat(navigation.getCartCount()).as("cart count").isEqualTo("1");
        assertThat(navigation.openCart().containsProduct(BACKPACK)).as("product in cart").isTrue();
    }

    @Test(description = "Removing the only product leaves the cart empty")
    public void shouldRemoveProductFromCart() {
        new CatalogPage(driver).waitUntilLoaded()
                .openProduct(BACKPACK)
                .addToCart();

        CartPage cart = new NavigationBar(driver).openCart().removeProduct();

        assertThat(cart.getEmptyCartTitle()).as("empty cart title").isEqualTo("No Items");
    }
}
