# SPAYD for KMP

[Short Payment Descriptor](https://cs.wikipedia.org/wiki/Short_Payment_Descriptor) generator for Kotlin Multiplatform. Generated SPAYD can be [forwarded to bank application](https://github.com/step-up-labs/pay-via-bank-app) or used to generate QR code.

## Integration

### Android

Add this to your dependencies:

`implementation 'io.stepuplabs.spaydkmp:android:<latest-version>'`

### iOS

Add dependency using Swift Package Manager:

#### Xcode

Go to you project and select Package Dependencies, click `+` in the lower left corner and add `https://github.com/step-up-labs/spayd-kmp-spm.git` in the upper right corner of the presented dialog (marked "Search or Enter Package URL").

#### Package.swift

Add a new entry into `dependencies`: `.package(url: "https://github.com/step-up-labs/spayd-kmp-spm.git", from: "<latest-version>"),`

## Usage

Create `Spayd` instance. The only mandatory parameter is an `account`. Example:

```kotlin
val spayd = Spayd(
    account = Account(iban = "IBAN"),
    amount = 666.00,
    currency = "CZK",
    message = "Diabolical",
)
```

Then call:

```kotlin
val spaydString = spayd.toString()
```

This will validate data and possibly throw `ValidationException` with short message describing first problem it encountered in case there is any issue.

### Alternative constructors
```kotlin
val spayd = Spayd(
    Key.ACCOUNT to Account(iban = "IBAN"),
    Key.AMOUNT to 666.00,
    Key.CURRENCY to "CZK",
    Key.MESSAGE to "Diabolical",
)
```

or

```kotlin
val parameters: Map<Key, Any> = mapOf(
    Key.ACCOUNT to Account(iban = "IBAN"),
    Key.AMOUNT to 666.00,
    Key.CURRENCY to "CZK",
    Key.MESSAGE to "Diabolical",
)

val spayd = Spayd(parameters)
```

## Contributing

For any contributions, make sure all unit tests pass. Ideally add a new unit tests. Any contributions via pull requests are welcome.