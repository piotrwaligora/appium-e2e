# App under test: Sauce Labs My Demo App

Analysis done before writing the framework (roadmap point 0).
Sources: app repositories and release builds, checked on 2026-11-26.

## Builds

|                   | Android                                                       | iOS                                                                      |
|-------------------|---------------------------------------------------------------|--------------------------------------------------------------------------|
| Repository        | [saucelabs/my-demo-app-android](https://github.com/saucelabs/my-demo-app-android) | [saucelabs/my-demo-app-ios](https://github.com/saucelabs/my-demo-app-ios) |
| Release           | `2.3.0` (2026-09-23)                                          | `2.3.0` (2026-09-23)                                                     |
| Asset             | `mda-2.3.0-27.apk` (17 MB)                                    | `SauceLabs-Demo-App.Simulator.zip` (6 MB, `Payload/My Demo App.app`)     |
| App id            | `com.saucelabs.mydemoapp.android`                             | `com.saucelabs.mydemo.app.ios`                                           |
| SDK / OS          | minSdk 21, targetSdk 31                                       | iOS 16.6+, built with Xcode 15.4 (iOS 17.5 SDK)                          |
| Architectures     | `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`                   | universal simulator binary: `x86_64` + `arm64`                           |
| Build type        | debug (`assembleDebug`), so WebView debugging is available   | simulator build                                                          |

Conclusions:
- The APK runs on a current x86_64 emulator (Android 14+ only blocks targetSdk < 23).
- The iOS simulator build runs on Apple Silicon GitHub runners (`macos-14`/`macos-15`), no Rosetta needed.
- Both builds are downloaded from GitHub releases; they are not committed to this repository.

## Navigation: the main structural difference

| Action           | Android                                      | iOS                               |
|------------------|----------------------------------------------|-----------------------------------|
| Open cart        | header cart icon (`cartRL`, "View cart")     | tab bar `Cart-tab-item`           |
| Open catalog     | menu → "Catalog"                             | tab bar `Catalog-tab-item`        |
| Open menu        | header `menuIV` ("View menu"), drawer        | tab bar `More-tab-item`           |
| Menu items       | text in `menuRV` list, e.g. "Log In"         | `Login Button`, `ResetAppState-menu-item`, `LogOut-menu-item`, ... |
| Sorting          | header `sortIV` → dialog                     | buttons on the catalog screen     |
| Validation errors| inline text under the field                  | alert "Validation Error!"         |

Most actions are still one or two taps on both platforms, so a locator pair per element covers them.
Validation errors are the exception: different UI, so the page object needs a platform-specific check there.

## Screens and locators

Android: `resource-id` is `com.saucelabs.mydemoapp.android:id/<id>`; content-desc (accessibility id) where the app sets one.
iOS: accessibility identifiers from storyboards and from Sauce Labs' own XCUITest page objects
(`MyDemoAppUITests/PageObjects/PageObject.swift`), so they are confirmed. "?" means no identifier: to be found in the page source from CI.

### Catalog
| Element            | Android                                  | iOS                                   |
|--------------------|------------------------------------------|---------------------------------------|
| Screen             | `productTV` (text "Products")            | `Catalog-screen`                      |
| Product list       | `productRV`                              | -                                     |
| Product tile       | item in `productRV` (`productIV`)        | `ProductItem` (by index)              |
| Product name       | `titleTV`                                | `Product Name` / static text by name  |
| Product price      | `priceTV`                                | `Product Price`                       |
| Sort button        | `sortIV`                                 | ? (sort buttons on screen)            |
| Sort options       | `nameAscCL`, `nameDesCL`, `priceAscCL`, `priceDesCL` | ?                         |

### Product details
| Element            | Android                                  | iOS                                   |
|--------------------|------------------------------------------|---------------------------------------|
| Screen             | `productTV` (product name)               | `ProductDetails-screen`               |
| Price              | `priceTV`                                | `Price`                               |
| Quantity           | `noTV`, `minusIV`, `plusIV`              | `Amount`, `SubtractMinus Icons`, `AddPlus Icons` |
| Color              | items in `colorRV`                       | `<Color>ColorUnSelected Icons`        |
| Add to cart        | `cartBt` ("Tap to add product to cart")  | `AddToCart` (label "Add To Cart")     |

### Cart
| Element            | Android                                  | iOS                                   |
|--------------------|------------------------------------------|---------------------------------------|
| Screen             | `productTV` ("My Cart")                  | `Cart-screen`                         |
| Badge (item count) | `cartTV` in the header                   | cart count label `cartCountLbl` (no id, ?) |
| Empty cart         | `noItemTitleTV` ("No Items"), `shoppingBt` | "No Items", `GoShopping`            |
| Item rows          | `productRV` → `titleTV`, `priceTV`, `noTV`, `removeBt` | static text by product name, "Remove Item" |
| Totals             | `itemsTV`, `totalPriceTV`                | ?                                     |
| Proceed            | `cartBt` ("Confirms products for checkout") | `ProceedToCheckout`                |

### Login
| Element            | Android                                  | iOS                                   |
|--------------------|------------------------------------------|---------------------------------------|
| Username / password| `nameET`, `passwordET`                   | ? (text fields without id)            |
| Login button       | `loginBtn`                               | ? (button "Login")                    |
| Test users         | `username1TV` bod@example.com, `username2TV` alice (locked out) | user list fills the form |
| Errors             | `nameErrorTV`, `passwordErrorTV`         | alert                                 |

Test password for all users: `10203040`. `alice@example.com` is locked out on both platforms.

### Checkout
| Step               | Android                                         | iOS                                     |
|--------------------|-------------------------------------------------|-----------------------------------------|
| Shipping address   | `fullNameET`, `address1ET`, `address2ET`, `cityET`, `stateET`, `zipET`, `countryET`, `paymentBtn` | `ShippingAddress-screen`, fields by placeholder, "To Payment" |
| Payment            | `nameET`, `cardNumberET`, `expirationDateET`, `securityCodeET`, `billingAddressCB`, `paymentBtn` | `Payment-screen`, fields by placeholder, "Review Order" |
| Review order       | `placeOrderRV`, `totalAmountTV`, `paymentBtn`   | "Place Order"                           |
| Complete           | `completeTV` ("Checkout Complete"), `shoopingBt` | `CheckoutComplete-screen`, `ContinueShopping` |

Checkout requires a logged-in user (Android sends the user to login first when proceeding from the cart).

## Test data

Product catalog is built into both apps (no backend):
Sauce Labs Backpack 29.99, Bike Light 9.99, Bolt T-Shirt 15.99, Onesie 7.99, Test.allTheThings() T-Shirt 15.99,
and the fleece product, which differs: **"Sauce Labs Fleece Jacket" on Android, "Sauce Labs Fleece T-Shirt" on iOS** (49.99).
Tests should avoid that product or keep names per platform.

## App state and reset

- Android keeps the cart and the logged-in flag in memory (`SingletonClass`), products in a local Room database.
  Restarting the app process (`terminateApp` + `activateApp`) gives an empty cart and a logged-out user.
- Both apps also have a "Reset App State" menu item (Android: confirmation dialog "RESET APP").
- iOS keeps the cart count in a singleton (`Engine.sharedInstance`) as well, so a restart most likely resets it too;
  to be confirmed in CI. `noReset=false` / reinstall is the safe fallback.
- Deep links are not used (project decision).

## WebView

Both apps have a WebView screen (menu → WebView, enter URL). Not part of the shop flows.
The Android APK is a debug build, so a `WEBVIEW_*` context would be available if we ever test it.

## Candidate scenarios

1. Catalog shows products; sorting by name/price changes the order.
2. Product details: open a product, check name and price, change quantity.
3. Cart: add a product, badge and cart content; remove the product, empty cart.
4. Login: valid user, locked-out user, missing username/password.
5. Checkout: logged-in user completes an order end to end.
