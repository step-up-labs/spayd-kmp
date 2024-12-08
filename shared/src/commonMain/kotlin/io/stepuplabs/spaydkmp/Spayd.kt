package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.*
import io.stepuplabs.spaydkmp.exception.DataException
import io.stepuplabs.spaydkmp.exception.ValidationException
import kotlinx.datetime.LocalDate

interface Spayd {
    // spayd base

    @Throws(DataException::class)
    fun setCzechAccount(prefix: Long?, account: Long, bank: Long): Spayd

    fun setAccount(iban: String, bic: String?): Spayd

    fun setAccount(account: Account): Spayd

    @Throws(DataException::class)
    fun addAlternateAccounts(account: Account): Spayd

    @Throws(DataException::class)
    fun setAlternateAccounts(accounts: Array<Account>): Spayd

    @Throws(DataException::class)
    fun setAmount(amount: Double): Spayd

    @Throws(DataException::class)
    fun setCurrency(currency: String): Spayd

    @Throws(DataException::class)
    fun setSenderReference(reference: Int): Spayd

    @Throws(DataException::class)
    fun setRecipientName(name: String): Spayd

    fun setDate(year: Int, month: Int, day: Int): Spayd

    fun setDate(date: LocalDate): Spayd

    @Throws(DataException::class)
    fun setPaymentType(type: String): Spayd

    @Throws(DataException::class)
    fun setMessage(message: String): Spayd

    @Throws(DataException::class)
    fun setNotificationEmail(email: String): Spayd

    @Throws(DataException::class)
    fun setNotificationPhone(phone: String): Spayd

    // czech payment extension

    @Throws(DataException::class)
    fun setRepeat(repeat: Int): Spayd

    @Throws(DataException::class)
    fun setVariableSymbol(symbol: Int): Spayd

    @Throws(DataException::class)
    fun setSpecificSymbol(symbol: Int): Spayd

    @Throws(DataException::class)
    fun setConstantSymbol(symbol: Int): Spayd

    @Throws(DataException::class)
    fun setIdentifier(identifier: String): Spayd

    @Throws(DataException::class)
    fun setUrl(url: String): Spayd

    // generate simple payment descriptor
    @Throws(ValidationException::class)
    fun create(): String
}