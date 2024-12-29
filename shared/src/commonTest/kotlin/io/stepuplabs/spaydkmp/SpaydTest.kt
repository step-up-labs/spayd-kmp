package io.stepuplabs.spaydkmp

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.stepuplabs.spaydkmp.common.BankAccount
import io.stepuplabs.spaydkmp.common.BankAccountList
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
           Key.BANK_ACCOUNT to BankAccount("CZ7603000000000076327632"),
        )
        val actual = spayd.toString()

        assertEquals(expected, actual)
    }

    @Test
    fun fullParameterSetPrimaryConstructor() {
        // copy of what's mentioned in README.md
        val expected = "SPD*1.0*DT:20251211*CC:CZK*AM:25.12*ACC:CZ7603000000000076327632*ALT-ACC:CZ7603000000000076327632,CZ7603000000000076327632*RF:1000001*RN:CLOVEK V TISNI*PT:TYP*MSG:DONATION*NT:P*NTA:+420321654987*X-PER:3*X-VS:9*X-SS:9*X-KS:9*X-ID:ID*X-URL:HTTPS://STEPUPLABS.IO"

        val altBankAccounts: List<BankAccount> = listOf(
            BankAccount("CZ7603000000000076327632"),
            BankAccount("CZ7603000000000076327632"),
        )

        val spayd = Spayd(
            Key.DUE_DATE to LocalDate(2025, 12, 11),
            Key.CURRENCY_CODE to "CZK",
            Key.AMOUNT to BigDecimal.fromFloat(25.12F),
            Key.BANK_ACCOUNT to BankAccount("CZ7603000000000076327632"),
            Key.ALTERNATIVE_BANK_ACCOUNTS to BankAccountList(altBankAccounts),
            Key.REFERENCE_FOR_RECIPIENT to 1000001,
            Key.RECIPIENT_NAME to "CLOVEK V TISNI",
            Key.PAYMENT_TYPE to "TYP",
            Key.MESSAGE to "DONATION",
            Key.NOTIFY_TYPE to NotificationType.PHONE,
            Key.NOTIFY_ADDRESS to "+420321654987",
            Key.DAYS_TO_REPEAT_IF_UNSUCCESSFUL to 3,
            Key.VARIABLE_SYMBOL to 9L,
            Key.SPECIFIC_SYMBOL to 9L,
            Key.CONSTANT_SYMBOL to 9L,
            Key.REFERENCE_FOR_SENDER to "ID",
            Key.URL to "https://stepuplabs.io",
        )
        val actual = spayd.toString()

        assertEquals(expected, actual)
    }

    @Test
    fun fullParameterSetSecondaryConstructor() {
        // copy of what's mentioned in README.md
        val expected = "SPD*1.0*ACC:CZ7603000000000076327632*ALT-ACC:CZ7603000000000076327632,CZ7603000000000076327632*CC:CZK*AM:25.12*DT:20251211*RF:1000001*RN:CLOVEK V TISNI*PT:TYP*MSG:DONATION*NT:P*NTA:+420321654987*X-PER:3*X-VS:9*X-SS:9*X-KS:9*X-ID:ID*X-URL:HTTPS://STEPUPLABS.IO"

        val altBankAccounts: List<BankAccount> = listOf(
            BankAccount("CZ7603000000000076327632"),
            BankAccount("CZ7603000000000076327632"),
        )
        val spayd = Spayd(
            bankAccount = BankAccount("CZ7603000000000076327632"),
            alternativeBankAccounts = BankAccountList(altBankAccounts),
            currencyCode = "CZK",
            amount = BigDecimal.fromFloat(25.12F),
            dueDate = LocalDate(2025, 12, 11),
            referenceForRecipient = 1000001,
            recipientName = "CLOVEK V TISNI",
            paymentType = "TYP",
            message = "DONATION",
            notificationType = NotificationType.PHONE,
            notificationAddress = "+420321654987",
            daysToRepeatIfUnsuccessfull = 3,
            variableSymbol = 9L,
            specificSymbol = 9L,
            constantSymbol = 9L,
            referenceForSender = "ID",
            url = "https://stepuplabs.io",
        )
        val actual = spayd.toString()

        assertEquals(expected, actual)
    }
}