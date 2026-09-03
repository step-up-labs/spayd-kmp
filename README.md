# SPAYD for KMP

[Short Payment Descriptor](https://cs.wikipedia.org/wiki/Short_Payment_Descriptor) generator for Kotlin Multiplatform. Generated SPAYD can be [forwarded to a bank application](https://github.com/step-up-labs/pay-via-bank-app) or used to generate a QR code.

## Integration

### Android

Add this to your dependencies:

`implementation 'io.stepuplabs.spaydkmp:spayd-kmp-android:<latest-version>'`

### iOS

Add dependency using Swift Package Manager:

#### Xcode

Go to you project and select Package Dependencies, click `+` in the lower left corner and add `https://github.com/step-up-labs/spayd-kmp-spm.git` in the upper right corner of the presented dialog (marked "Search or Enter Package URL").

#### Package.swift

Add a new entry into `dependencies`: `.package(url: "https://github.com/step-up-labs/spayd-kmp-spm.git", from: "<latest-version>"),`

## Usage

Create `Spayd` instance. The only mandatory parameter is `account`. Example:

```kotlin
val spayd = Spayd(
    bankAccount = BankAccount(iban = "CZ7603000000000076327632"),
    amount = "500.00".toBigDecimal(),
    currencyCode = "CZK",
    message = "Clovek v tisni",
)
```

Then call:

```kotlin
val spaydString = spayd.toString()
```

This will validate data and possibly throw `ValidationException` with a short message describing the first problem it encountered.

### Bank account creation and validation

It's common that you have a bank account as plain string editable by users in your app. In this case use helper function:

```kotlin
BankAccountUtils.parse(bankAccountString)
```

to parse or validate bank account in Czech or IBAN format.

You can also create/validate bank account from Czech bank account format like this:

```kotlin
BankAccountUtils.createFromCzechAccount(prefix, accountNumber, bankCode)
```

Lastly, you can validate IBAN string by calling:

```kotlin
BankAccountUtils.validateIban(ibanString)
```

### Alternative constructors
```kotlin
val spayd = Spayd(
    Key.BANK_ACCOUNT to BankAccount(iban = "CZ7603000000000076327632"),
    Key.AMOUNT to "500.00".toBigDecimal(),
    Key.CURRENCY_CODE to "CZK",
    Key.MESSAGE to "Clovek v tisni",
)
```

or

```kotlin
val parameters: Map<Key, Any> = mapOf(
    Key.BANK_ACCOUNT to BankAccount(iban = "CZ7603000000000076327632"),
    Key.AMOUNT to "500.00".toBigDecimal(),
    Key.CURRENCY_CODE to "CZK",
    Key.MESSAGE to "Clovek v tisni",
)

val spayd = Spayd(parameters)
```

## Contributing

For any contributions, make sure all unit tests pass. Ideally, add one or more new unit tests. Any contributions via pull requests are welcome.
