# SPAYD for KMP

Multiplatform SPAYD generator

# Usage

Create Spayd instance. The only mandatory parameter is an `account`. You cen either use named parameters to fill all necessary values, or you can create all pairs yourself and then pass them on.

```kotlin
val spayd = Spayd(
    account = Account(iban = "IBAN"),
    amount = 666.00,
    currency = "CZK",
    message = "Diabolical",
)
```

or

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

Generate SPAYD payload that can be forwarded to bank application or used to generate QR code.

```kotlin
val payload = "$spayd"
```

or

```kotlin
val payload = spayd.toString()
```

Creating SPAYD string will also validate data and throw `ValidationException` with short message describing first problem it encountered in case there is any issue.