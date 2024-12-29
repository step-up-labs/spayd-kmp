package io.stepuplabs.spaydkmp.common

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.LocalDate
import kotlin.reflect.KClass

/*
Specification of a parameter
 */
@Suppress("UNUSED")
enum class Key(
    val key: String,
    val type: KClass<*>,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
) {
    DUE_DATE(key = "DT", type = LocalDate::class),
    CURRENCY_CODE(key = "CC", type = String::class, minLength = 3, maxLength = 3),
    AMOUNT(key = "AM", type = BigDecimal::class, minValue = 0.00, maxValue = 9_999_999.99),
    BANK_ACCOUNT(key = "ACC", type = BankAccount::class),
    ALTERNATIVE_BANK_ACCOUNTS(key = "ALT-ACC", type = BankAccountList::class, maxLength = 2),
    REFERENCE_FOR_RECIPIENT(key = "RF", type = Int::class, maxLength = 16),
    RECIPIENT_NAME(key = "RN", type = String::class, maxLength = 35),
    PAYMENT_TYPE(key = "PT", type = String::class, maxLength = 3),
    MESSAGE(key = "MSG", type = String::class, maxLength = 60),
    NOTIFY_TYPE(key = "NT", type = NotificationType::class),
    NOTIFY_ADDRESS(key = "NTA", type = String::class, maxLength = 320),
    DAYS_TO_REPEAT_IF_UNSUCCESSFUL(
        key = "X-PER",
        type = Int::class,
        minValue = 0.0,
        maxValue = 30.0,
    ),
    VARIABLE_SYMBOL(key = "X-VS", type = Long::class, maxLength = 10),
    SPECIFIC_SYMBOL(key = "X-SS", type = Long::class, maxLength = 10),
    CONSTANT_SYMBOL(key = "X-KS", type = Long::class, maxLength = 10),
    REFERENCE_FOR_SENDER(key = "X-ID", type = String::class, maxLength = 20),
    URL(key = "X-URL", type = String::class, maxLength = 40),
}