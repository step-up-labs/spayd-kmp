package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.Account
import io.stepuplabs.spaydkmp.common.AccountList
import io.stepuplabs.spaydkmp.common.NotificationType
import io.stepuplabs.spaydkmp.exception.*
import io.stepuplabs.spaydkmp.value.Kind
import io.stepuplabs.spaydkmp.value.Value
import kotlin.math.log10
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import net.thauvin.erik.urlencoder.UrlEncoderUtil

@Suppress("UNUSED")
class Spayd(
    private vararg val values: Value?
) {
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
        values = arrayOf(
            Value(kind = Kind.ACCOUNT, value = account),
            alternateAccounts?.let { Value(kind = Kind.ALTERNATE_ACCOUNTS, value = it) },
            currency?.let { Value(kind = Kind.CURRENCY, value = it) },
            amount?.let { Value(kind = Kind.AMOUNT, value = it) },
            date?.let { Value(kind = Kind.DATE, value = it) },
            senderReference?.let { Value(kind = Kind.SENDER_REFERENCE, value = it) },
            recipientName?.let { Value(kind = Kind.RECIPIENT_NAME, value = it) },
            paymentType?.let { Value(kind = Kind.PAYMENT_TYPE, value = it) },
            message?.let { Value(kind = Kind.MESSAGE, value = it) },
            notificationType?.let { Value(kind = Kind.NOTIFY_TYPE, value = it) },
            notificationAddress?.let { Value(kind = Kind.NOTIFY_ADDRESS, value = it) },
            repeat?.let { Value(kind = Kind.REPEAT, value = it) },
            variableSymbol?.let { Value(kind = Kind.VARIABLE_SYMBOL, value = it) },
            specificSymbol?.let { Value(kind = Kind.SPECIFIC_SYMBOL, value = it) },
            constantSymbol?.let { Value(kind = Kind.CONSTANT_SYMBOL, value = it) },
            identifier?.let { Value(kind = Kind.IDENTIFIER, value = it) },
            url?.let { Value(kind = Kind.URL, value = it) },
        )
    )

    @Throws(ValidationException::class)
    fun generate(): String {
        validateValueSet()

        val parts: MutableList<String> = mutableListOf()

        // format header
        parts.add(HEADER_TYPE)
        parts.add(HEADER_VERSION)

        // payment parameters
        for (value in values.filterNotNull()) {
            getEntry(value.kind.key, value.value)?.let { parts.add(it) }
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
    private fun validateValueSet() {
        if (values.isEmpty()) {
            throw ValidationException("At least account has to be specified")
        }

        var hasAccount = false
        var hasNotificationType = false
        var hasNotificationAddress = false

        for (value in values.filterNotNull()) {
            value.validate()

            when (value.kind) {
                Kind.ACCOUNT -> hasAccount = true
                Kind.NOTIFY_TYPE -> hasNotificationType = true
                Kind.NOTIFY_ADDRESS -> hasNotificationAddress = true
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