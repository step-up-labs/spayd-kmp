# SPAYD for KMP

Multiplatform SPAYD generator

## Architecture: Idea

The architecture will be refactored once I have any idea how this will be used and what exactly it needs to do :)

Create a factory instance:

```kotlin
val spayd = SpaydFactory()
```

Set all parameters necessary:

```kotlin
spayd
    .setAccount(iban = "IBAN", bic = "BIC")
    .setAmount(amount = 666.00)
    // Set the rest of parameters
```

If given parameter can't be used, method will throw `DataException` with short explanation as to why not

Generate SPAYD:

```kotlin
val payload = spayd.create()
// Launch bank app or generate QR code from obtained payload
```

`.create()` will return either valid SPAYD or throw `ValidationException` in case SPAYD couldn't be created