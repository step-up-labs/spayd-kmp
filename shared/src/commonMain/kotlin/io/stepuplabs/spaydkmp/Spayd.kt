package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.*
import io.stepuplabs.spaydkmp.exception.*
import kotlin.math.log10
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import net.thauvin.erik.urlencoder.UrlEncoderUtil

@Suppress("UNUSED")
class Spayd(
    private var account: Account,
    private var alternateAccounts: List<Account>? = null,
    private var amount: Double? = null,
    private var currency: String? = null,
    private var senderReference: Int? = null,
    private var recipientName: String? = null,
    private var date: LocalDate? = null,
    private var paymentType: String? = null,
    private var message: String? = null,
    private var notification: Notification? = null,
    private var repeat: Int? = null,
    private var variableSymbol: Int? = null,
    private var specificSymbol: Int? = null,
    private var constantSymbol: Int? = null,
    private var identifier: String? = null,
    private var url: String? = null,
) {
    @Throws(ValidationException::class)
    fun validate(): Boolean {
        alternateAccounts?.let {
            if (it.count() >= 2) {
                throw ValidationException(message = "Alternate account: Maximum allowed items = 2")
            }
        }

        amount?.let {
            if (it > 9_999_999.99) {
                throw ValidationException(message = "Amount: Maximum = 9,999,999.99")
            }
        }

        currency?.let {
            if (it.length != 3) {
                throw ValidationException(message = "Currency: Allowed format = ISO 4217 (three characters)")
            }
        }

        senderReference?.let {
            if (digitsOf(it) > 16) {
                throw ValidationException(message = "Sender reference: Maximum allowed digits = 16")
            }
        }

        recipientName?.let {
            if (it.length > 35) {
                throw ValidationException(message = "Recipient name: Maximum allowed length = 35")
            }
        }

        paymentType?.let {
            if (it.length > 3) {
                throw ValidationException(message = "Payment type: Maximum allowed length = 3")
            }
        }

        message?.let {
            if (it.length > 60) {
                throw ValidationException(message = "Message: Maximum allowed length = 60")
            }
        }

       notification?.let { notification ->
            when (notification.type) {
                NotificationType.EMAIL -> {
                    if (notification.address.length > 320) {
                        throw ValidationException(message = "Notification e-mail: Maximum allowed length = 320")
                    }
                }

                NotificationType.PHONE -> {
                    if (notification.address.length > 32) {
                        throw ValidationException(message = "Notification phone: Maximum allowed length = 32")
                    }
                }
            }
        }

        repeat?.let {
            if (it < 0 || it > 30) {
                throw ValidationException(message = "Repeat: Allowed range = 0..30")
            }
        }

        variableSymbol?.let {
            if (digitsOf(it) > 10) {
                throw ValidationException(message = "Variable symbol: Maximum allowed digits = 10")
            }
        }

        specificSymbol?.let {
            if (digitsOf(it) > 10) {
                throw ValidationException(message = "Specific symbol: Maximum allowed digits = 10")
            }
        }

        constantSymbol?.let {
            if (digitsOf(it) > 10) {
                throw ValidationException(message = "Constant symbol: Maximum allowed digits = 10")
            }
        }

        identifier?.let {
            if (it.length > 20) {
                throw ValidationException(message = "Identifier: Maximum allowed length = 20")
            }
        }

        url?.let {
            if (it.length > 140) {
                throw ValidationException(message = "URL: Maximum allowed length = 140")
            }
        }

        return true
    }

    @Throws(ValidationException::class)
    fun generate(): String {
        validate()

        val values: MutableList<String> = mutableListOf()

        // format header
        values.add(HEADER_TYPE)
        values.add(HEADER_VERSION)

        // spayd base
        getEntry(PARAM_DATE, date)?.let { values.add(it) }
        getEntry(PARAM_CURRENCY, currency)?.let { values.add(it) }
        getEntry(PARAM_AMOUNT, amount)?.let { values.add(it) }
        getEntry(PARAM_ACCOUNT, account)?.let { values.add(it) }
        getEntry(PARAM_ALTERNATE_ACCOUNTS, alternateAccounts)?.let { values.add(it) }
        getEntry(PARAM_SENDER_REFERENCE, senderReference)?.let { values.add(it) }
        getEntry(PARAM_RECIPIENT_NAME, recipientName)?.let { values.add(it) }
        getEntry(PARAM_PAYMENT_TYPE, paymentType)?.let { values.add(it) }
        getEntry(PARAM_MESSAGE, message)?.let { values.add(it) }
        notification?.let { notification ->
            getEntry(PARAM_NOTIFY_TYPE, notification.type)?.let { values.add(it) }
            getEntry(PARAM_NOTIFY_ADDRESS, notification.address)?.let { values.add(it) }
        }

        // czech payment extension
        getEntry(PARAM_CZX_REPEAT, repeat)?.let { values.add(it) }
        getEntry(PARAM_CZX_VARIABLE_SYMBOL, variableSymbol)?.let { values.add(it) }
        getEntry(PARAM_CZX_SPECIFIC_SYMBOL, specificSymbol)?.let { values.add(it) }
        getEntry(PARAM_CZX_CONSTANT_SYMBOL, constantSymbol)?.let { values.add(it) }
        getEntry(PARAM_CZX_IDENTIFIER, identifier)?.let { values.add(it) }
        getEntry(PARAM_CZX_URL, url)?.let { values.add(it) }

        // merge into one string
        val entries = StringBuilder()
        for (value in values) {
            if (entries.isNotEmpty()) {
                entries.append("*")
            }
            entries.append(value)
        }

        return entries.toString().uppercase()
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

        const val PARAM_DATE: String = "DT"
        const val PARAM_CURRENCY: String = "CC"
        const val PARAM_AMOUNT: String = "AM"
        const val PARAM_ACCOUNT: String = "ACC"
        const val PARAM_ALTERNATE_ACCOUNTS: String = "ALT-ACC"
        const val PARAM_SENDER_REFERENCE: String = "RF"
        const val PARAM_RECIPIENT_NAME: String = "RN"
        const val PARAM_PAYMENT_TYPE: String = "PT"
        const val PARAM_MESSAGE: String = "MSG"
        const val PARAM_NOTIFY_TYPE: String = "NT"
        const val PARAM_NOTIFY_ADDRESS: String = "NTA"
        const val PARAM_CZX_REPEAT: String = "X-PER"
        const val PARAM_CZX_VARIABLE_SYMBOL: String = "X-VS"
        const val PARAM_CZX_SPECIFIC_SYMBOL: String = "X-SS"
        const val PARAM_CZX_CONSTANT_SYMBOL: String = "X-KS"
        const val PARAM_CZX_IDENTIFIER: String = "X-ID"
        const val PARAM_CZX_URL: String = "X-URL"
    }
}