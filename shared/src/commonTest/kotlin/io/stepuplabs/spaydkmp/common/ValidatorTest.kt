package io.stepuplabs.spaydkmp.common

import io.stepuplabs.spaydkmp.exception.ValidationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ValidatorTest {
    private val validator = Validator()

    @Test
    fun type() {
        assertEquals(
            validator.validate(value = Account(iban = "XXX"), Key.ACCOUNT),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "IBAN", Key.ACCOUNT) // too short
        }
    }

    @Test
    fun currency() {
        assertEquals(
            validator.validate(value = "CZE", Key.CURRENCY),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "CZ", Key.CURRENCY) // too short
        }

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "CZECH", Key.CURRENCY) // too long
        }
    }

    @Test
    fun amount() {
        assertEquals(
            validator.validate(value = 500.0, Key.AMOUNT),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = -20.0, Key.AMOUNT) // too low
        }

        assertFailsWith(ValidationException::class) {
            validator.validate(value = 10_000_000.0, Key.AMOUNT) // too high
        }
    }

    @Test
    fun alternateAccounts() {
        assertEquals(
            validator.validate(
                value = AccountList(accounts = listOf()),
                Key.ALTERNATE_ACCOUNTS
            ),
            true,
        )

        assertEquals(
            validator.validate(
                value = AccountList(
                    accounts = listOf(Account(iban = "XXX"), Account(iban = "YYY")),
                ),
                Key.ALTERNATE_ACCOUNTS
            ),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(
                value = AccountList(
                    accounts = listOf(
                        Account(iban = "XXX"),
                        Account(iban = "YYY"),
                        Account(iban = "ZZZZ"),
                    ),
                ),
                Key.ALTERNATE_ACCOUNTS
            )
        }
    }

    @Test
    fun senderReference() {
        assertEquals(
            validator.validate(value = 123456, Key.SENDER_REFERENCE),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = 12345678901234567, Key.SENDER_REFERENCE) // too long
        }
    }

    @Test
    fun recipientName() {
        assertEquals(
            validator.validate(value = "recipient name", Key.RECIPIENT_NAME),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate( // too long
                value = "recipient name, recipient name, recipient name, recipient name",
                Key.RECIPIENT_NAME,
            )
        }
    }

    @Test
    fun paymentType() {
        assertEquals(
            validator.validate(value = "PTY", Key.PAYMENT_TYPE),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "Payment TYpe", Key.PAYMENT_TYPE) // too long
        }
    }

    @Test
    fun message() {
        assertEquals(
            validator.validate(value = "fairly long message that still fits", Key.MESSAGE),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate( // too long
                value = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eleifend.",
                Key.MESSAGE,
            )
        }
    }

    @Test
    fun notifyAddress() {
        assertEquals(
            validator.validate(value = "some_longish_email@example.com", Key.NOTIFY_ADDRESS),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate( // too long
                value = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet elit metus. Maecenas eu nulla id purus bibendum pulvinar. Suspendisse pretium leo blandit rutrum blandit. Nulla a ante placerat, feugiat tellus at, consequat elit. Nunc in dapibus magna. Duis at luctus quam. Class aptent taciti sociosqu ad litora torquent per conubia justo.",
                Key.NOTIFY_ADDRESS,
            )
        }
    }

    @Test
    fun repeat() {
        assertEquals(
            validator.validate(value = 3, Key.REPEAT),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = -1, Key.REPEAT) // too low
        }

        assertFailsWith(ValidationException::class) {
            validator.validate(value = 33, Key.REPEAT) // too high
        }
    }

    @Test
    fun variableSymbol() {
        assertEquals(
            validator.validate(value = 123456L, Key.VARIABLE_SYMBOL),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = 123456789012L, Key.VARIABLE_SYMBOL) // too long
        }
    }

    @Test
    fun specificSymbol() {
        assertEquals(
            validator.validate(value = 123456L, Key.SPECIFIC_SYMBOL),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = 123456789012L, Key.SPECIFIC_SYMBOL) // too long
        }
    }

    @Test
    fun constantSymbol() {
        assertEquals(
            validator.validate(value = 123456L, Key.CONSTANT_SYMBOL),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = 123456789012L, Key.CONSTANT_SYMBOL) // too long
        }
    }

    @Test
    fun identifier() {
        assertEquals(
            validator.validate(value = "IDENTIFIER", Key.IDENTIFIER),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "ABCDEFGHIJKLMNOPQRSTUVWXYZ", Key.IDENTIFIER) // too long
        }
    }

    @Test
    fun url() {
        assertEquals(
            validator.validate(value = "URL", Key.URL),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate( // too long
                value = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
                Key.URL
            )
        }
    }
}