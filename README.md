# SPAYD for KMP

Multiplatform SPAYD generator

# Usage

Create Spayd instance. The only mandatory parameter is `account`. You either can use named parameters to fill all necessary values, or you can create all `Values` yourself and then pass them on.

```kotlin
val values: MutableList<Value> = mutableListOf()
values.add(Value(Kind.ACCOUNT, Account(iban = "IBAN")))
values.add(Value(Kind.AMOUNT, 666.00))
values.add(Value(Kind.CURRENCY, "CZK"))
values.add(Value(Kind.MESSAGE, "Diabolical"))

val spayd = Spayd(values = values.toTypedArray())
```

or

```kotlin
val spayd = Spayd(
    Value(Kind.ACCOUNT, Account(iban = "IBAN")),
    Value(Kind.AMOUNT, 666.00),
    Value(Kind.CURRENCY, "CZK"),
    Value(Kind.MESSAGE, "Diabolical"),
)
```

or

```kotlin
val spayd = Spayd(
    account = Account(iban = "IBAN"),
    amount = 666.00,
    currency = "CZK",
    message = "Diabolical",
)
```

Generate SPAYD payload that can be forwarded to bank application or used to generate QR code.

```kotlin
val payload = spayd.generate()
```

`generate()` will also validate data and throw `ValidationException` with short message describing first problem it encountered in case there is any issue.