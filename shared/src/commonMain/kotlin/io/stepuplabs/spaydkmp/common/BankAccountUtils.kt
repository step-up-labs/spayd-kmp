package io.stepuplabs.spaydkmp.common

import io.stepuplabs.spaydkmp.formatter.Formatter
import kotlin.math.min

object BankAccountUtils {

    /**
     * Parse any string to bank account. Returns valid bank account if it's in Czech or IBAN format.
     * If it's invalid, returns null. Use this function for bank account validation.
     */
    fun parse(bankAccountString: String): BankAccount? {
        val czechBankAccount = parseCzechBankAccount(bankAccountString)
        return czechBankAccount
            ?: if (validateIban(bankAccountString)) {
                BankAccount(bankAccountString)
            } else {
                null
            }
    }

    /**
     * Create bank account from Czech bank account
     * returns null if account is invalid
     */
    fun createFromCzechAccount(
        prefix: Long?,
        accountNumber: Long,
        bankCode: Long
    ): BankAccount? {
        val iban = createIbanForCzechAccount(prefix, accountNumber, bankCode)
        return if (iban != null && validateIban(iban)) {
            BankAccount(iban)
        } else {
            null
        }
    }

    /**
     * Validates IBAN. Returns true if valid.
     * Logic taken from library https://github.com/barend/java-iban
     */
    fun validateIban(iban: String): Boolean {
        if (iban.isEmpty()) {
            return false
        }
        if (!isLetterOrDigit(iban.first()) || !isLetterOrDigit(iban.last())) {
            return false
        }

        val plainIban = iban.replace(" ", "")

        if (plainIban.length < 5) {
            return false
        }

        if (plainIban[2] !in '0'..'9' || plainIban[3] !in '0'..'9') {
            return false
        }

        val countryCode = plainIban.substring(0, 2).uppercase()
        val countryIndex = COUNTRY_CODES.indexOf(countryCode)
        if (countryIndex < 0) {
            return false
        }

        val expectedLength = COUNTRY_IBAN_LENGTHS[countryIndex]
        if (plainIban.length != expectedLength) {
            return false
        }

        return verifyModulo97(plainIban)
    }

    private fun isLetterOrDigit(c: Char): Boolean {
        return c in '0'..'9' || c in 'A'..'Z' || c in 'a'..'z'
    }

    private fun verifyModulo97(plainIban: String): Boolean {
        var remainder = 0
        for (i in 4 until plainIban.length) {
            remainder = processCharForModulo97(plainIban[i], remainder) ?: return false
        }
        for (i in 0 until 4) {
            remainder = processCharForModulo97(plainIban[i], remainder) ?: return false
        }
        return remainder == 1
    }

    private fun processCharForModulo97(c: Char, currentRemainder: Int): Int? {
        return when (c) {
            in '0'..'9' -> (currentRemainder * 10 + (c - '0')) % 97
            in 'A'..'Z' -> (currentRemainder * 100 + (10 + (c - 'A'))) % 97
            in 'a'..'z' -> (currentRemainder * 100 + (10 + (c - 'a'))) % 97
            else -> null
        }
    }

    private val COUNTRY_CODES = arrayOf(
        "AD", "AE", "AL", "AO", "AT", "AZ", "BA", "BE", "BF", "BG",
        "BH", "BI", "BJ", "BR", "BY", "CF", "CG", "CH", "CI", "CM",
        "CR", "CV", "CY", "CZ", "DE", "DJ", "DK", "DO", "DZ", "EE",
        "EG", "ES", "FI", "FO", "FR", "GA", "GB", "GE", "GI", "GL",
        "GQ", "GR", "GT", "GW", "HN", "HR", "HU", "IE", "IL", "IQ",
        "IR", "IS", "IT", "JO", "KM", "KW", "KZ", "LB", "LC", "LI",
        "LT", "LU", "LV", "LY", "MA", "MC", "MD", "ME", "MG", "MK",
        "ML", "MR", "MT", "MU", "MZ", "NE", "NI", "NL", "NO", "PK",
        "PL", "PS", "PT", "QA", "RO", "RS", "SA", "SC", "SD", "SE",
        "SI", "SK", "SM", "SN", "ST", "SV", "TD", "TG", "TL", "TN",
        "TR", "UA", "VA", "VG", "XK",
    )

    private val COUNTRY_IBAN_LENGTHS = intArrayOf(
        24, 23, 28, 25, 20, 28, 20, 16, 28, 22,
        22, 16, 28, 29, 28, 27, 27, 21, 28, 27,
        22, 25, 28, 24, 22, 27, 18, 28, 26, 20,
        29, 24, 18, 18, 27, 27, 22, 22, 23, 18,
        27, 27, 28, 25, 28, 21, 28, 22, 23, 23,
        26, 26, 27, 30, 27, 30, 20, 28, 32, 21,
        20, 20, 21, 25, 28, 27, 24, 22, 27, 19,
        28, 27, 31, 30, 25, 28, 32, 18, 15, 24,
        28, 29, 25, 29, 24, 22, 24, 31, 18, 24,
        19, 24, 27, 28, 25, 28, 27, 28, 23, 24,
        26, 29, 22, 24, 20,
    )

    /**
     * Generates IBAN from Czech bank account
     * returns null if account is invalid
     */
    private fun createIbanForCzechAccount(prefix: Long?, account: Long, bank: Long): String? {
        var isValid = true
        if (prefix != null) {
            isValid = validateEleven(prefix)
        }
        isValid = isValid && validateEleven(account)

        if (!isValid) {
            return null
        }

        val prefixFormatted: String = Formatter.format("%06d", prefix ?: 0)
        val accountFormatted: String = Formatter.format("%010d", account)
        val bankFormatted: String = Formatter.format("%04d", bank)

        val buf = bankFormatted + prefixFormatted + accountFormatted + "123500"
        var index = 0
        var dividend: String
        var checksum = -1

        while (index <= buf.length) {
            if (checksum < 0) {
                dividend = buf.substring(
                    index,
                    min((index + 9).toDouble(), buf.length.toDouble()).toInt()
                )

                index += 9
            } else if (checksum in 0..9) {
                dividend = checksum.toString() + buf.substring(
                    index,
                    min((index + 8).toDouble(), buf.length.toDouble()).toInt()
                )

                index += 8
            } else {
                dividend = checksum.toString() + buf.substring(
                    index,
                    min((index + 7).toDouble(), buf.length.toDouble()).toInt()
                )

                index += 7
            }
            checksum = dividend.toInt() % 97
        }
        checksum = 98 - checksum

        val accountForIban =
            Formatter.format("%02d", checksum) + bankFormatted + prefixFormatted + accountFormatted
        return "CZ$accountForIban"
    }

    // Validate account prefix and account number
    private fun validateEleven(value: Long): Boolean {
        val number = value.toString()
        var weight = 1
        var sum = 0

        for (i in number.length - 1 downTo 0 step 1) {
            sum += (number[i] - '0') * weight
            weight *= 2
        }

        return sum % 11 == 0
    }

    private fun parseCzechBankAccount(bankAccount: String): BankAccount? {
        if (!bankAccount.contains("/")) {
            return null
        }
        return try {
            val parts = bankAccount.trim().split("/")
            val bankCode = parts[1].toLong()
            val accountNumberParts = parts[0].split("-")
            val accountNumber = if (accountNumberParts.size == 1) {
                accountNumberParts[0].toLong()
            } else {
                accountNumberParts[1].toLong()
            }
            val prefix: Long? = if (accountNumberParts.size == 1) {
                null
            } else {
                accountNumberParts[0].toLong()
            }
            createFromCzechAccount(prefix, accountNumber, bankCode)
        } catch (e: Throwable) {
            null
        }
    }
}