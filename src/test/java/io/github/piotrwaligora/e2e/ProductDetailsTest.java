package io.github.piotrwaligora.e2e;

import io.github.piotrwaligora.e2e.ui.pages.CatalogPage;
import io.github.piotrwaligora.e2e.ui.pages.ProductDetailsPage;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDetailsTest extends BaseTest {

    @Test(description = "Product opened from the catalog shows its name and price")
    public void shouldShowProductDetails() {
        ProductDetailsPage details = new CatalogPage(driver).waitUntilLoaded()
                .openProduct("Sauce Labs Backpack");

        assertThat(details.getName()).as("product name").isEqualTo("Sauce Labs Backpack");
        assertThat(details.getPrice()).as("product price").isEqualTo("$ 29.99");
    }
}
