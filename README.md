# SPAYD for KMP

Multiplatform SPAYD generator

# Usage

Create Spayd instance.  The only mandatory parameter is `account`.

```kotlin
val spayd = Spayd(
    Value(Kind.ACCOUNT, Account(iban = "IBAN")),
    Value(Kind.AMOUNT, 666.00),
    Value(Kind.CURRENCY, "CZK"),
    Value(Kind.MESSAGE, "Diabolical")
)
```

Generate SPAYD payload that can be forwarded to bank application or used to generate QR code.

```kotlin
val payload = spayd.generate()
```

`generate()` will also validate data and throw `ValidationException` with short message describing first problem it encountered in case there is any issue.