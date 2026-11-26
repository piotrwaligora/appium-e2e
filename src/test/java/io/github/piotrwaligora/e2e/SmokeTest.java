package io.github.piotrwaligora.e2e;

import io.github.piotrwaligora.e2e.ui.pages.CatalogPage;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SmokeTest extends BaseTest {

    @Test(description = "App starts and shows the product catalog")
    public void shouldShowCatalogAfterStart() {
        CatalogPage catalog = new CatalogPage(driver).waitUntilLoaded();

        assertThat(catalog.getTitle()).as("catalog title").isEqualTo("Products");
    }
}
