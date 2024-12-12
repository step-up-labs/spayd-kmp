package io.stepuplabs.spaydkmp.common

import io.stepuplabs.spaydkmp.exception.ValidationException
import io.stepuplabs.spaydkmp.formatter.Formatter
import kotlin.math.min

/*
Utility class for creation an IBAN code from account prefix, account number and bank code
This works only for Czech banking system
 */
class IBAN {
    // Generate IBAN code for Czech account
    @Throws(ValidationException::class)
    fun createForCzechAccount(prefix: Long?, account: Long, bank: Long): String {
        var isValid = false
        if (prefix != null) {
            isValid = validateEleven(prefix)
        }
        isValid = isValid && validateEleven(account)

        if (!isValid) {
            throw ValidationException(message = "Account prefix & number: Invalid value")
        }

        val prefixFormatted: String = Formatter.format("%06d", prefix ?: "000000")
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

        return "CZ" + Formatter.format("%02d", checksum) + bankFormatted + prefixFormatted + accountFormatted
    }

    // Validate account prefix and account number
    private fun validateEleven(value: Long): Boolean {
        val number = value.toString()
        var weight = 1
        var sum = 0

        for (i in 0.. number.length step -1) {
            sum += (number[i] - '0') * weight
            weight *= 2
        }

        return sum % 11 == 0
    }
}