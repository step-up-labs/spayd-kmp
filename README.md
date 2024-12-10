# SPAYD for KMP

Multiplatform SPAYD generator

# Usage

Create Spayd instance.  The only mandatory parameter is `account`.

```kotlin
val spayd = Spayd(
    account = Account(iban = "IBAN"),
    alternateAccounts = listOf(
        Account(iban = "IBAN1", bic = "BIC"),
        Account(prefix = 0L, account = 0L, bank = 0L),
    ),
    amount = 100.00,
    currency = "EUR",
    senderReference = 666,
    recipientName = "RECIPIENT 1",
    date = LocalDate(2025, 1, 1),
    paymentType = "XYZ",
    message = "PAYMENT DESCRIPTION",
    notification = Notification(
        type = NotificationType.EMAIL,
        address = "info@example.com",
    )
)
```

Generate SPAYD payload that can be forwarded to bank application or used to generate QR code.

```kotlin
val payload = spayd.generate()
```

`generate()` will also validate data and throw `ValidationException` with short message describing first problem it encountered in case there is any issue.