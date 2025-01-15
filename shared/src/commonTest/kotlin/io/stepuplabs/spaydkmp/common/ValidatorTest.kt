package io.stepuplabs.spaydkmp.common

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import io.stepuplabs.spaydkmp.exception.ValidationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ValidatorTest {
    private val validator = Validator()

    @Test
    fun type() {
        assertEquals(
            validator.validate(value = BankAccount(iban = "XXX"), Key.BANK_ACCOUNT),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "IBAN", Key.BANK_ACCOUNT) // too short
        }
    }

    @Test
    fun currency() {
        assertEquals(
            validator.validate(value = "CZE", Key.CURRENCY_CODE),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "CZ", Key.CURRENCY_CODE) // too short
        }

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "CZECH", Key.CURRENCY_CODE) // too long
        }
    }

    @Test
    fun amount() {
        assertEquals(
            validator.validate(value = "500.00".toBigDecimal(), Key.AMOUNT),
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
                value = BankAccountList(bankAccounts = listOf()),
                Key.ALTERNATIVE_BANK_ACCOUNTS
            ),
            true,
        )

        assertEquals(
            validator.validate(
                value = BankAccountList(
                    bankAccounts = listOf(BankAccount(iban = "XXX"), BankAccount(iban = "YYY")),
                ),
                Key.ALTERNATIVE_BANK_ACCOUNTS
            ),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(
                value = BankAccountList(
                    bankAccounts = listOf(
                        BankAccount(iban = "XXX"),
                        BankAccount(iban = "YYY"),
                        BankAccount(iban = "ZZZZ"),
                    ),
                ),
                Key.ALTERNATIVE_BANK_ACCOUNTS
            )
        }
    }

    @Test
    fun senderReference() {
        assertEquals(
            validator.validate(value = 123456, Key.REFERENCE_FOR_RECIPIENT),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = 12345678901234567, Key.REFERENCE_FOR_RECIPIENT) // too long
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
            validator.validate(value = 3, Key.DAYS_TO_REPEAT_IF_UNSUCCESSFUL),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = -1, Key.DAYS_TO_REPEAT_IF_UNSUCCESSFUL) // too low
        }

        assertFailsWith(ValidationException::class) {
            validator.validate(value = 33, Key.DAYS_TO_REPEAT_IF_UNSUCCESSFUL) // too high
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
            validator.validate(value = "IDENTIFIER", Key.REFERENCE_FOR_SENDER),
            true,
        )

        assertFailsWith(ValidationException::class) {
            validator.validate(value = "ABCDEFGHIJKLMNOPQRSTUVWXYZ", Key.REFERENCE_FOR_SENDER) // too long
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