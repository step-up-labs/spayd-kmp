package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.*
import io.stepuplabs.spaydkmp.exception.*
import kotlin.math.log10
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import net.thauvin.erik.urlencoder.UrlEncoderUtil

@Suppress("UNUSED")
class SpaydFactory: Spayd {
    private val iban = IBAN()

    private var account: Account? = null
    private var alternateAccounts: MutableList<Account> = mutableListOf()
    private var amount: Double? = null
    private var currency: String? = null
    private var senderReference: Int? = null
    private var recipientName: String? = null
    private var date: LocalDate? = null
    private var paymentType: String? = null
    private var message: String? = null
    private var notificationType: String? = null
    private var notificationAddress: String? = null
    private var repeat: Int? = null
    private var variableSymbol: Int? = null
    private var specificSymbol: Int? = null
    private var constantSymbol: Int? = null
    private var identifier: String? = null
    private var url: String? = null

    @Throws(DataException::class)
    override fun setCzechAccount(prefix: Long?, account: Long, bank: Long): Spayd {
        try {
            val value = iban.createForCzechAccount(prefix, account, bank)
            this.setAccount(Account(iban = value))
        } catch (exception: DataException) {
            throw exception
        }

        return this
    }

    override fun setAccount(iban: String, bic: String?): Spayd {
        val account = Account(iban = iban, bic = bic)
        this.setAccount(account)

        return this
    }

    override fun setAccount(account: Account): Spayd {
        this.account = account

        return this
    }

    @Throws(DataException::class)
    override fun addAlternateAccounts(account: Account): Spayd {
        if (alternateAccounts.count() >= 2) {
            throw DataException(message = "Alternate account: Maximum allowed items = 2")
        }

        alternateAccounts.add(account)

        return this
    }

    @Throws(DataException::class)
    override fun setAlternateAccounts(accounts: Array<Account>): Spayd {
        if (accounts.count() > 2) {
            throw DataException(message = "Alternate account: Maximum allowed items = 2")
        }

        alternateAccounts.addAll(accounts)

        return this
    }

    @Throws(DataException::class)
    override fun setAmount(amount: Double): Spayd {
        if (amount > 9_999_999.99) {
            throw DataException(message = "Amount: Maximum = 9,999,999.99")
        }

        this.amount = amount

        return this
    }

    @Throws(DataException::class)
    override fun setCurrency(currency: String): Spayd {
        if (currency.length != 3) {
            throw DataException(message = "Currency: Allowed format = ISO 4217 (three characters)")
        }

        this.currency = currency.uppercase()

        return this
    }

    @Throws(DataException::class)
    override fun setSenderReference(reference: Int): Spayd {
        if (digitsOf(reference) > 16) {
            throw DataException(message = "Sender reference: Maximum allowed digits = 16")
        }

        this.senderReference = reference

        return this
    }

    @Throws(DataException::class)
    override fun setRecipientName(name: String): Spayd {
        val trimmed = name.trim()
        if (trimmed.length > 35) {
            throw DataException(message = "Recipient name: Maximum allowed length = 35")
        }

        this.recipientName = trimmed

        return this
    }

    override fun setDate(year: Int, month: Int, day: Int): Spayd {
        this.setDate(
            LocalDate(year, month, day)
        )

        return this
    }

    override fun setDate(date: LocalDate): Spayd {
        this.date = date

        return this
    }

    @Throws(DataException::class)
    override fun setPaymentType(type: String): Spayd {
        val trimmed = type.trim()
        if (trimmed.length > 3) {
            throw DataException(message = "Payment type: Maximum allowed length = 3")
        }

        this.paymentType = trimmed

        return this
    }

    @Throws(DataException::class)
    override fun setMessage(message: String): Spayd {
        val trimmed = message.trim()
        if (trimmed.length > 60) {
            throw DataException(message = "Message: Maximum allowed length = 60")
        }

        this.message = trimmed

        return this
    }

    @Throws(DataException::class)
    override fun setNotificationEmail(email: String): Spayd {
        val trimmed = email.trim()
        if (trimmed.length > 320) {
            throw DataException(message = "Notification e-mail: Maximum allowed length = 320")
        }

        this.notificationType = NOTIFICATION_TYPE_EMAIL
        this.notificationAddress = trimmed

        return this
    }

    @Throws(DataException::class)
    override fun setNotificationPhone(phone: String): Spayd {
        val trimmed = phone.trim()
        if (trimmed.length > 32) {
            throw DataException(message = "Notification phone: Maximum allowed length = 32")
        }

        this.notificationType = NOTIFICATION_TYPE_PHONE
        this.notificationAddress = trimmed

        return this
    }

    @Throws(DataException::class)
    override fun setRepeat(repeat: Int): Spayd {
        if (repeat < 0 || repeat > 30) {
            throw DataException(message = "Repeat: Allowed range = 0..30")
        }

        this.repeat = repeat

        return this
    }

    @Throws(DataException::class)
    override fun setVariableSymbol(symbol: Int): Spayd {
        if (digitsOf(symbol) > 10) {
            throw DataException(message = "Variable symbol: Maximum allowed digits = 10")
        }

        this.variableSymbol = symbol

        return this
    }

    @Throws(DataException::class)
    override fun setSpecificSymbol(symbol: Int): Spayd {
        if (digitsOf(symbol) > 10) {
            throw DataException(message = "Specific symbol: Maximum allowed digits = 10")
        }

        this.specificSymbol = symbol

        return this
    }

    @Throws(DataException::class)
    override fun setConstantSymbol(symbol: Int): Spayd {
        if (digitsOf(symbol) > 10) {
            throw DataException(message = "Constant symbol: Maximum allowed digits = 10")
        }

        this.constantSymbol = symbol

        return this
    }

    @Throws(DataException::class)
    override fun setIdentifier(identifier: String): Spayd {
        val trimmed = identifier.trim()
        if (trimmed.length > 20) {
            throw DataException(message = "Identifier: Maximum allowed length = 20")
        }

        this.identifier = trimmed

        return this
    }

    @Throws(DataException::class)
    override fun setUrl(url: String): Spayd {
        val trimmed = url.trim()
        if (trimmed.length > 140) {
            throw DataException(message = "URL: Maximum allowed length = 140")
        }

        this.url = trimmed

        return this
    }

    @Throws(ValidationException::class)
    override fun create(): String {
        if (account == null) {
            throw ValidationException(message = "Missing account info")
        }

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
        if (notificationType != null && notificationAddress != null) {
            getEntry(PARAM_NOTIFY_TYPE, notificationType)?.let { values.add(it) }
            getEntry(PARAM_NOTIFY_ADDRESS, notificationAddress)?.let { values.add(it) }
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

        const val NOTIFICATION_TYPE_EMAIL = "E"
        const val NOTIFICATION_TYPE_PHONE = "P"
    }
}