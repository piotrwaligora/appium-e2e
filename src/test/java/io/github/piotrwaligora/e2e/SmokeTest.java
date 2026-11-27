package io.github.piotrwaligora.e2e;

import io.github.piotrwaligora.e2e.ui.pages.CatalogPage;
import org.testng.annotations.Test;

import static io.github.piotrwaligora.e2e.data.TestData.BACKPACK;
import static org.assertj.core.api.Assertions.assertThat;

public class SmokeTest extends BaseTest {

    @Test(description = "App starts and shows the product catalog")
    public void shouldShowCatalogAfterStart() {
        CatalogPage catalog = new CatalogPage(driver).waitUntilLoaded();

        assertThat(catalog.containsProduct(BACKPACK)).as("product in the catalog").isTrue();
    }
}
