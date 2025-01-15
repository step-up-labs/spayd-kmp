package io.stepuplabs.spaydkmp

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.stepuplabs.spaydkmp.common.BankAccount
import io.stepuplabs.spaydkmp.common.BankAccountList
import io.stepuplabs.spaydkmp.common.Key
import io.stepuplabs.spaydkmp.common.NotificationType
import io.stepuplabs.spaydkmp.common.PaymentType
import io.stepuplabs.spaydkmp.common.Validator
import io.stepuplabs.spaydkmp.exception.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import net.thauvin.erik.urlencoder.UrlEncoderUtil

/*
Class that represents and generates SPAYD
 */
@Suppress("UNUSED")
class Spayd(
    private vararg val parameters: Pair<Key, Any>?
) {
    // Convenience constructor that accepts map of parameters
    constructor(parameters: Map<Key, Any>): this(
        parameters = parameters.mapNotNull { it.key to it.value }.toTypedArray()
    )

    // Convenience constructor that accepts all values in form of named parameters
    constructor(
        bankAccount: BankAccount,
        alternativeBankAccounts: BankAccountList? = null,
        currencyCode: String? = null,
        amount: BigDecimal? = null,
        dueDate: LocalDate? = null,
        referenceForRecipient: Int? = null,
        recipientName: String? = null,
        paymentType: PaymentType? = null,
        message: String? = null,
        notificationType: NotificationType? = null,
        notificationAddress: String? = null,
        daysToRepeatIfUnsuccessfull: Int? = null,
        variableSymbol: Long? = null,
        specificSymbol: Long? = null,
        constantSymbol: Long? = null,
        referenceForSender: String? = null,
        url: String? = null,
    ): this(
        parameters = arrayOf(
            Key.BANK_ACCOUNT to bankAccount,
            alternativeBankAccounts?.let { Key.ALTERNATIVE_BANK_ACCOUNTS to it },
            currencyCode?.let { Key.CURRENCY_CODE to it },
            amount?.let { Key.AMOUNT to it },
            dueDate?.let { Key.DUE_DATE to it },
            referenceForRecipient?.let { Key.REFERENCE_FOR_RECIPIENT to it },
            recipientName?.let { Key.RECIPIENT_NAME to it },
            paymentType?.let { Key.PAYMENT_TYPE to it },
            message?.let { Key.MESSAGE to it },
            notificationType?.let { Key.NOTIFY_TYPE to it },
            notificationAddress?.let { Key.NOTIFY_ADDRESS to it },
            daysToRepeatIfUnsuccessfull?.let { Key.DAYS_TO_REPEAT_IF_UNSUCCESSFUL to it },
            variableSymbol?.let { Key.VARIABLE_SYMBOL to it },
            specificSymbol?.let { Key.SPECIFIC_SYMBOL to it },
            constantSymbol?.let { Key.CONSTANT_SYMBOL to it },
            referenceForSender?.let { Key.REFERENCE_FOR_SENDER to it },
            url?.let { Key.URL to it },
        )
    )

    // Validate parameters and generate SPAYD string from them
    override fun toString(): String {
        validateParameters()

        val parts: MutableList<String> = mutableListOf()

        // format header
        parts.add(HEADER_TYPE)
        parts.add(HEADER_VERSION)

        // payment parameters
        for (parameter in parameters.filterNotNull()) {
            getEntry(parameter.first, parameter.second)?.let { parts.add(it) }
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

    // Validate all entered parameters
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
                Key.BANK_ACCOUNT -> hasAccount = true
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

    // Get parameter:value key for SPAYD
    private fun getEntry(parameter: Key, value: Any?): String? {
        if (value == null) {
            return null
        }

        val valStr = when (parameter.type) {
            LocalDate::class -> (value as LocalDate).format(LocalDate.Formats.ISO_BASIC)
            BigDecimal::class -> (value as BigDecimal).toStringExpanded()
            else -> sanitize("$value")
        }

        return "${parameter.key}:$valStr"
    }

    // Get parameter:value key for SPAYD
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
                sanitize(value.toString()),
            )
        }

        return "$parameter:$entries"
    }

    // Sanitize values for SPAYD
    private fun sanitize(value: String): String = Regex("[^A-Za-z0-9 @$%+\\-/:.,]")
        .replace(value, "")

    companion object {
        const val MIME_TYPE: String = "application/x-shortpaymentdescriptor"
        const val FILE_EXTENSION: String = "spayd"

        private const val HEADER_TYPE: String = "SPD"
        private const val HEADER_VERSION: String = "1.0"
    }
}