package io.stepuplabs.spaydkmp.value

import io.stepuplabs.spaydkmp.common.Account
import io.stepuplabs.spaydkmp.common.AccountList
import io.stepuplabs.spaydkmp.common.NotificationType
import io.stepuplabs.spaydkmp.exception.ValidationException
import kotlinx.datetime.LocalDate
import kotlin.math.log10

data class Value(
    val kind: Kind,
    val value: Any,
) {
    @Throws(ValidationException::class)
    fun validate(): Boolean {
        if (!kind.type.isInstance(value)) {
            throw ValidationException(
                "Value class is incorrect. Is: ${value::class.simpleName}, should be: ${kind.type.simpleName}",
            )
        }

        when (kind.type) {
            LocalDate::class -> return true
            Account::class -> return true
            NotificationType::class -> return true

            Int::class -> {
                val typedValue = value as Int

                kind.minValue?.let {
                    if (typedValue < it) {
                        throw ValidationException("$kind is lower than allowed minimum value ($it)")
                    }
                }
                kind.maxValue?.let {
                    if (typedValue > it) {
                        throw ValidationException("$kind is higher than allowed maximum value ($it)")
                    }
                }
                kind.minLength?.let {
                    if (digitsOf(typedValue) < it) {
                        throw ValidationException("$kind is shorter than allowed minimum length ($it)")
                    }
                }
                kind.maxLength?.let {
                    if (digitsOf(typedValue) > it) {
                        throw ValidationException("$kind is longer than allowed maximum length ($it)")
                    }
                }
            }

            Long::class -> {
                val typedValue = value as Long

                kind.minValue?.let {
                    if (typedValue < it) {
                        throw ValidationException("$kind is lower than allowed minimum value ($it)")
                    }
                }
                kind.maxValue?.let {
                    if (typedValue > it) {
                        throw ValidationException("$kind is higher than allowed maximum value ($it)")
                    }
                }
                kind.minLength?.let {
                    if (digitsOf(typedValue) < it) {
                        throw ValidationException("$kind is shorter than allowed minimum length ($it)")
                    }
                }
                kind.maxLength?.let {
                    if (digitsOf(typedValue) > it) {
                        throw ValidationException("$kind is longer than allowed maximum length ($it)")
                    }
                }
            }

            Double::class -> {
                val typedValue = value as Double

                kind.minValue?.let {
                    if (typedValue < it) {
                        throw ValidationException("$kind is lower than allowed minimum value ($it)")
                    }
                }
                kind.maxValue?.let {
                    if (typedValue > it) {
                        throw ValidationException("$kind is higher than allowed maximum value ($it)")
                    }
                }

                // length for double doesn't make much sense
            }

            String::class -> {
                val typedValue = value as String

                // min/max value for string doesn't make much sense

                kind.minLength?.let {
                    if (typedValue.length < it) {
                        throw ValidationException("$kind is shorter than allowed minimum length ($it)")
                    }
                }
                kind.maxLength?.let {
                    if (typedValue.length > it) {
                        throw ValidationException("$kind is longer than allowed maximum length ($it)")
                    }
                }
            }

            AccountList::class -> {
                val typedValue = value as AccountList

                // min/max value for list doesn't make much sense

                kind.minLength?.let {
                    if (typedValue.accounts.count() < it) {
                        throw ValidationException("$kind is shorter than allowed minimum length ($it)")
                    }
                }
                kind.maxLength?.let {
                    if (typedValue.accounts.count() > it) {
                        throw ValidationException("$kind is longer than allowed maximum length ($it)")
                    }
                }
            }

            else -> throw ValidationException("Unsupported type of $kind")
        }

        return true
    }

    private fun digitsOf(value: Int): Int =  log10((value).toDouble()).toInt() + 1
    private fun digitsOf(value: Long): Int =  log10((value).toDouble()).toInt() + 1
}