package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.Account
import io.stepuplabs.spaydkmp.common.AccountList
import io.stepuplabs.spaydkmp.common.Key
import io.stepuplabs.spaydkmp.common.NotificationType
import io.stepuplabs.spaydkmp.common.Validator
import io.stepuplabs.spaydkmp.exception.*
import kotlin.math.log10
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import net.thauvin.erik.urlencoder.UrlEncoderUtil

@Suppress("UNUSED")
class Spayd(
    private vararg val parameters: Pair<Key, Any>?
) {
    constructor(parameters: Map<Key, Any>): this(
        parameters = parameters.mapNotNull { it.key to it.value }.toTypedArray()
    )

    constructor(
        account: Account,
        alternateAccounts: AccountList? = null,
        currency: String? = null,
        amount: Double? = null,
        date: LocalDate? = null,
        senderReference: Int? = null,
        recipientName: String? = null,
        paymentType: String? = null,
        message: String? = null,
        notificationType: NotificationType? = null,
        notificationAddress: String? = null,
        repeat: Int? = null,
        variableSymbol: Long? = null,
        specificSymbol: Long? = null,
        constantSymbol: Long? = null,
        identifier: String? = null,
        url: String? = null,
    ): this(
        parameters = arrayOf(
            Key.ACCOUNT to account,
            alternateAccounts?.let { Key.ALTERNATE_ACCOUNTS to it },
            alternateAccounts?.let { Key.ALTERNATE_ACCOUNTS to it },
            currency?.let { Key.CURRENCY to it },
            amount?.let { Key.AMOUNT to it },
            date?.let { Key.DATE to it },
            senderReference?.let { Key.SENDER_REFERENCE to it },
            recipientName?.let { Key.RECIPIENT_NAME to it },
            paymentType?.let { Key.PAYMENT_TYPE to it },
            message?.let { Key.MESSAGE to it },
            notificationType?.let { Key.NOTIFY_TYPE to it },
            notificationAddress?.let { Key.NOTIFY_ADDRESS to it },
            repeat?.let { Key.REPEAT to it },
            variableSymbol?.let { Key.VARIABLE_SYMBOL to it },
            specificSymbol?.let { Key.SPECIFIC_SYMBOL to it },
            constantSymbol?.let { Key.CONSTANT_SYMBOL to it },
            identifier?.let { Key.IDENTIFIER to it },
            url?.let { Key.URL to it },
        )
    )

    override fun toString(): String {
        validateParameters()

        val parts: MutableList<String> = mutableListOf()

        // format header
        parts.add(HEADER_TYPE)
        parts.add(HEADER_VERSION)

        // payment parameters
        for (parameter in parameters.filterNotNull()) {
            getEntry(parameter.first.key, parameter.second)?.let { parts.add(it) }
        }

        // merge into one string
        val spayd = StringBuilder()
        for (part in parts) {
            if (spayd.isNotEmpty()) {
                spayd.append("*")
            }
            spayd.append(part)
        }

        return spayd.toString().uppercase()
    }

    @Throws(ValidationException::class)
    private fun validateParameters() {
        if (parameters.isEmpty()) {
            throw ValidationException("At least account has to be specified")
        }

        val validator = Validator()

        var hasAccount = false
        var hasNotificationType = false
        var hasNotificationAddress = false

        for (parameter in parameters.filterNotNull()) {
            validator.validate(key = parameter.first, value = parameter.second)

            when (parameter.first) {
                Key.ACCOUNT -> hasAccount = true
                Key.NOTIFY_TYPE -> hasNotificationType = true
                Key.NOTIFY_ADDRESS -> hasNotificationAddress = true
                else -> continue
            }
        }

        if (!hasAccount) {
            throw ValidationException("At least account has to be specified")
        }

        if (hasNotificationType != hasNotificationAddress) {
            throw ValidationException("When setting notification, both type & address has to be set")
        }
    }

    private fun getEntry(parameter: String, value: Any?): String? {
        if (value == null) {
            return null
        }

        return "$parameter:$value"
    }

    private fun getEntry(parameter: String, values: Array<Any>?): String? {
        if (values.isNullOrEmpty()) {
            return null
        }

        val entries = StringBuilder()
        for (value in values) {
            if (entries.isNotEmpty()) {
                entries.append(",")
            }

            entries.append(
                escape(value.toString()),
            )
        }

        return "$parameter:$entries"
    }


    private fun getEntry(parameter: String, date: LocalDate?): String? {
        if (date == null) {
            return null
        }

        return "$parameter:${date.format(LocalDate.Formats.ISO_BASIC)}"
    }

    private fun escape(value: String): String {
        val escapedValue = StringBuilder()

        for (char in value) {
            if (char.code > 127) {
                escapedValue.append(UrlEncoderUtil.encode(char.toString()))
            } else {
                if (char.compareTo('*') == 0) { // spayd value separator
                    escapedValue.append("%2A")
                } else if (char.compareTo('+') == 0) {
                    escapedValue.append("%2B")
                } else if (char.compareTo('%') == 0) {
                    escapedValue.append("%25")
                } else {
                    escapedValue.append(char)
                }
            }
        }

        return escapedValue.toString()
    }

    private fun digitsOf(value: Int): Int =  log10((value).toDouble()).toInt() + 1

    companion object {
        const val MIME_TYPE: String = "application/x-shortpaymentdescriptor"

        const val HEADER_TYPE: String = "SPD"
        const val HEADER_VERSION: String = "1.0"
    }
}