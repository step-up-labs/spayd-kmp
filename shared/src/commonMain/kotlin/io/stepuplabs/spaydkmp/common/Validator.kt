package io.stepuplabs.spaydkmp.common

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.stepuplabs.spaydkmp.exception.ValidationException
import kotlinx.datetime.LocalDate
import kotlin.math.log10

/*
Validator that validates given value against its definition
 */
internal class Validator {
    // Validate value
    @Throws(ValidationException::class)
    fun validate(value: Any, key: Key): Boolean {
        if (!key.type.isInstance(value)) {
            throw ValidationException(
                "Value class is incorrect. Is: ${value::class.simpleName}, should be: ${key.type.simpleName}",
            )
        }

        when (key.type) {
            LocalDate::class -> return true
            Account::class -> return true
            NotificationType::class -> return true

            Int::class -> {
                val typedValue = value as Int

                key.minValue?.let {
                    if (typedValue < it) {
                        throw ValidationException("$key is lower than allowed minimum value ($it)")
                    }
                }
                key.maxValue?.let {
                    if (typedValue > it) {
                        throw ValidationException("$key is higher than allowed maximum value ($it)")
                    }
                }
                key.minLength?.let {
                    if (digitsOf(typedValue) < it) {
                        throw ValidationException("$key is shorter than allowed minimum length ($it)")
                    }
                }
                key.maxLength?.let {
                    if (digitsOf(typedValue) > it) {
                        throw ValidationException("$key is longer than allowed maximum length ($it)")
                    }
                }
            }

            Long::class -> {
                val typedValue = value as Long

                key.minValue?.let {
                    if (typedValue < it) {
                        throw ValidationException("$key is lower than allowed minimum value ($it)")
                    }
                }
                key.maxValue?.let {
                    if (typedValue > it) {
                        throw ValidationException("$key is higher than allowed maximum value ($it)")
                    }
                }
                key.minLength?.let {
                    if (digitsOf(typedValue) < it) {
                        throw ValidationException("$key is shorter than allowed minimum length ($it)")
                    }
                }
                key.maxLength?.let {
                    if (digitsOf(typedValue) > it) {
                        throw ValidationException("$key is longer than allowed maximum length ($it)")
                    }
                }
            }

            Double::class -> {
                val typedValue = value as Double

                key.minValue?.let {
                    if (typedValue < it) {
                        throw ValidationException("$key is lower than allowed minimum value ($it)")
                    }
                }
                key.maxValue?.let {
                    if (typedValue > it) {
                        throw ValidationException("$key is higher than allowed maximum value ($it)")
                    }
                }

                // length for double doesn't make much sense
            }

            BigDecimal::class -> {
                val typedValue = value as BigDecimal

                key.minValue?.let {
                    if (typedValue < it) {
                        throw ValidationException("$key is lower than allowed minimum value ($it)")
                    }
                }
                key.maxValue?.let {
                    if (typedValue > it) {
                        throw ValidationException("$key is higher than allowed maximum value ($it)")
                    }
                }

                // length for big decimal doesn't make much sense
            }

            String::class -> {
                val typedValue = value as String

                // min/max value for string doesn't make much sense

                key.minLength?.let {
                    if (typedValue.length < it) {
                        throw ValidationException("$key is shorter than allowed minimum length ($it)")
                    }
                }
                key.maxLength?.let {
                    if (typedValue.length > it) {
                        throw ValidationException("$key is longer than allowed maximum length ($it)")
                    }
                }
            }

            AccountList::class -> {
                val typedValue = value as AccountList

                // min/max value for list doesn't make much sense

                key.minLength?.let {
                    if (typedValue.accounts.count() < it) {
                        throw ValidationException("$key is shorter than allowed minimum length ($it)")
                    }
                }
                key.maxLength?.let {
                    if (typedValue.accounts.count() > it) {
                        throw ValidationException("$key is longer than allowed maximum length ($it)")
                    }
                }
            }

            else -> throw ValidationException("Unsupported type of $key")
        }

        return true
    }

    // Calculate number of digits for integer value
    private fun digitsOf(value: Int): Int =  log10((value).toDouble()).toInt() + 1

    // Calculate number of digits for long value
    private fun digitsOf(value: Long): Int =  log10((value).toDouble()).toInt() + 1
}