package io.github.piotrwaligora.e2e.data;

/** Test data of the demo app: users, products, a shipping address and a payment card. */
public final class TestData {

    public record Address(String fullName, String addressLine, String city, String zipCode, String country) {
    }

    public record PaymentCard(String holder, String number, String expiration, String securityCode) {
    }

    public static final String BACKPACK = "Sauce Labs Backpack";
    public static final String BACKPACK_PRICE = "$ 29.99";

    public static final Address ADDRESS = new Address("Jan Kowalski", "Testowa 1", "Warszawa", "00-001", "Poland");

    /** Test card number; the app does not charge anything. */
    public static final PaymentCard CARD = new PaymentCard("Jan Kowalski", "4111111111111111", "0330", "123");

    private TestData() {
    }
}
