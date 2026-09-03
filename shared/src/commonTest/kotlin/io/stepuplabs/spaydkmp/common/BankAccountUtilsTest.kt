package io.stepuplabs.spaydkmp.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class BankAccountUtilsTest {

    @Test
    fun createCzechAccount() {
        val account = BankAccountUtils.createFromCzechAccount(null, accountNumber = 76327632, bankCode = 300)
        assertEquals(
            "CZ7603000000000076327632",
            account?.iban,
        )
    }

    @Test
    fun parseCzechBankAccount() {
        val account = BankAccountUtils.parse("76327632/0300")
        assertEquals(
            "CZ7603000000000076327632",
            account?.iban,
        )
    }

    @Test
    fun parseValidIban() {
        val account = BankAccountUtils.parse("CZ7603000000000076327632")
        assertEquals(
            "CZ7603000000000076327632",
            account?.iban,
        )
    }

    @Test
    fun parseInvalidIban() {
        val account = BankAccountUtils.parse("CZ7603000000000076327633")
        assertNull(account)
    }

    @Test
    fun testValidIbans() {
        assertTrue(BankAccountUtils.validateIban("CZ7603000000000076327632"))
        assertTrue(BankAccountUtils.validateIban("CZ76 0300 0000 0000 7632 7632"))
        assertTrue(BankAccountUtils.validateIban("DE89370400440532013000"))
        assertTrue(BankAccountUtils.validateIban("NL91ABNA0417164300"))
        assertTrue(BankAccountUtils.validateIban("GB29NWBK60161331926819"))
        assertTrue(BankAccountUtils.validateIban("cz7603000000000076327632"))
    }

    @Test
    fun testInvalidIbans() {
        assertFalse(BankAccountUtils.validateIban(""))
        assertFalse(BankAccountUtils.validateIban("   "))
        assertFalse(BankAccountUtils.validateIban("CZ7603000000000076327633")) // wrong check digit
        assertFalse(BankAccountUtils.validateIban("CZ760300000000007632763")) // wrong length
        assertFalse(BankAccountUtils.validateIban("XX7603000000000076327632")) // unknown country code
        assertFalse(BankAccountUtils.validateIban("CZXX03000000000076327632")) // non-numeric check digits
        assertFalse(BankAccountUtils.validateIban(" CZ7603000000000076327632")) // leading space
        assertFalse(BankAccountUtils.validateIban("CZ7603000000000076327632 ")) // trailing space
        assertFalse(BankAccountUtils.validateIban("CZ76")) // too short
        assertFalse(BankAccountUtils.validateIban("CZ760300000000007632763!")) // invalid character
    }
}
