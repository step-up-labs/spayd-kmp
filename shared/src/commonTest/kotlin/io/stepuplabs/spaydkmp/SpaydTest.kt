package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.Account
import io.stepuplabs.spaydkmp.common.AccountList
import io.stepuplabs.spaydkmp.common.Key
import io.stepuplabs.spaydkmp.common.NotificationType
import io.stepuplabs.spaydkmp.exception.ValidationException
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class SpaydTest {
    @Test
    fun notEnoughParameters() {
        assertFailsWith(ValidationException::class) {
            Spayd().toString()
        }
    }

    @Test
    fun minimalParameterSet() {
        val expected = "SPD*1.0*ACC:CZ7603000000000076327632"

        val spayd = Spayd(
           Key.ACCOUNT to Account("CZ7603000000000076327632"),
        )
        val actual = spayd.toString()

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

        val spayd = Spayd(
            Key.DATE to LocalDate(2025, 12, 11),
            Key.CURRENCY to "CZK",
            Key.AMOUNT to 25.00,
            Key.ACCOUNT to Account("CZ7603000000000076327632"),
            Key.ALTERNATE_ACCOUNTS to AccountList(altAccounts),
            Key.SENDER_REFERENCE to 1000001,
            Key.RECIPIENT_NAME to "CLOVEK V TISNI",
            Key.PAYMENT_TYPE to "TYP",
            Key.MESSAGE to "DONATION",
            Key.NOTIFY_TYPE to NotificationType.PHONE,
            Key.NOTIFY_ADDRESS to "+420321654987",
            Key.REPEAT to 3,
            Key.VARIABLE_SYMBOL to 9L,
            Key.SPECIFIC_SYMBOL to 9L,
            Key.CONSTANT_SYMBOL to 9L,
            Key.IDENTIFIER to "ID",
            Key.URL to "https://stepuplabs.io",
        )
        val actual = spayd.toString()

        assertEquals(expected, actual)
    }

    @Test
    fun fullParameterSetSecondaryConstructor() {
        // copy of what's mentioned in README.md
        val expected = "SPD*1.0*ACC:CZ7603000000000076327632*ALT-ACC:CZ7603000000000076327632,CZ7603000000000076327632*ALT-ACC:CZ7603000000000076327632,CZ7603000000000076327632*CC:CZK*AM:25.0*DT:2025-12-11*RF:1000001*RN:CLOVEK V TISNI*PT:TYP*MSG:DONATION*NT:P*NTA:+420321654987*X-PER:3*X-VS:9*X-SS:9*X-KS:9*X-ID:ID*X-URL:HTTPS://STEPUPLABS.IO"

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
        val actual = spayd.toString()

        assertEquals(expected, actual)
    }
}