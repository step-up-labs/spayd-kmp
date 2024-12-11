package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.Account
import io.stepuplabs.spaydkmp.common.AccountList
import io.stepuplabs.spaydkmp.common.NotificationType
import io.stepuplabs.spaydkmp.exception.ValidationException
import io.stepuplabs.spaydkmp.value.Kind
import io.stepuplabs.spaydkmp.value.Value
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class SpaydTest {
    @Test
    fun notEnoughParameters() {
        assertFailsWith(ValidationException::class) {
            Spayd().generate()
        }
    }

    @Test
    fun minimalParameterSet() {
        val expected = "SPD*1.0*ACC:CZ7603000000000076327632"

        val spayd = Spayd(
            Value(kind = Kind.ACCOUNT, value = Account("CZ7603000000000076327632"))
        )
        val actual = spayd.generate()

        assertEquals(expected, actual)
    }

    @Test
    fun fullParameterSetPrimaryConstructor() {
        // copy of what's mentioned in README.md
        val expected = "SPD*1.0*DT:2025-12-11*CC:CZK*AM:25.0*ACC:CZ7603000000000076327632*ALT-ACC:CZ7603000000000076327632,CZ7603000000000076327632*RF:1000001*RN:CLOVEK V TISNI*PT:TYP*MSG:DONATION*NT:P*NTA:+420321654987*X-PER:3*X-VS:9*X-SS:9*X-KS:9*X-ID:ID*X-URL:HTTPS://STEPUPLABS.IO"

        val altAccounts: List<Account> = listOf(
            Account("CZ7603000000000076327632"),
            Account("CZ7603000000000076327632"),
        )
        val values: MutableList<Value> = mutableListOf()
        values.add(Value(kind = Kind.DATE, value = LocalDate(2025, 12, 11)))
        values.add(Value(kind = Kind.CURRENCY, value = "CZK"))
        values.add(Value(kind = Kind.AMOUNT, value = 25.00))
        values.add(Value(kind = Kind.ACCOUNT, value = Account("CZ7603000000000076327632")))
        values.add(Value(kind = Kind.ALTERNATE_ACCOUNTS, value = AccountList(altAccounts)))
        values.add(Value(kind = Kind.SENDER_REFERENCE, value = 1000001))
        values.add(Value(kind = Kind.RECIPIENT_NAME, value = "CLOVEK V TISNI"))
        values.add(Value(kind = Kind.PAYMENT_TYPE, value = "TYP"))
        values.add(Value(kind = Kind.MESSAGE, value = "DONATION"))
        values.add(Value(kind = Kind.NOTIFY_TYPE, value = NotificationType.PHONE))
        values.add(Value(kind = Kind.NOTIFY_ADDRESS, value = "+420321654987"))
        values.add(Value(kind = Kind.REPEAT, value = 3))
        values.add(Value(kind = Kind.VARIABLE_SYMBOL, value = 9L))
        values.add(Value(kind = Kind.SPECIFIC_SYMBOL, value = 9L))
        values.add(Value(kind = Kind.CONSTANT_SYMBOL, value = 9L))
        values.add(Value(kind = Kind.IDENTIFIER, value = "ID"))
        values.add(Value(kind = Kind.URL, value = "https://stepuplabs.io"))

        val spayd = Spayd(values = values.toTypedArray())
        val actual = spayd.generate()

        assertEquals(expected, actual)
    }

    @Test
    fun fullParameterSetSecondaryConstructor() {
        // copy of what's mentioned in README.md
        val expected = "SPD*1.0*ACC:CZ7603000000000076327632*ALT-ACC:CZ7603000000000076327632,CZ7603000000000076327632*CC:CZK*AM:25.0*DT:2025-12-11*RF:1000001*RN:CLOVEK V TISNI*PT:TYP*MSG:DONATION*NT:P*NTA:+420321654987*X-PER:3*X-VS:9*X-SS:9*X-KS:9*X-ID:ID*X-URL:HTTPS://STEPUPLABS.IO"

        val altAccounts: List<Account> = listOf(
            Account("CZ7603000000000076327632"),
            Account("CZ7603000000000076327632"),
        )
        val spayd = Spayd(
            account = Account("CZ7603000000000076327632"),
            alternateAccounts = AccountList(altAccounts),
            currency = "CZK",
            amount = 25.00,
            date = LocalDate(2025, 12, 11),
            senderReference = 1000001,
            recipientName = "CLOVEK V TISNI",
            paymentType = "TYP",
            message = "DONATION",
            notificationType = NotificationType.PHONE,
            notificationAddress = "+420321654987",
            repeat = 3,
            variableSymbol = 9L,
            specificSymbol = 9L,
            constantSymbol = 9L,
            identifier = "ID",
            url = "https://stepuplabs.io",
        )
        val actual = spayd.generate()

        assertEquals(expected, actual)
    }
}