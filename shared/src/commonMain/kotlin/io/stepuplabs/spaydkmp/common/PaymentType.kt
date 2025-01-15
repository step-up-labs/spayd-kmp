package io.stepuplabs.spaydkmp.common

/*
Payment type representation
 */
@Suppress("UNUSED")
enum class PaymentType(val key: String) {
    IMMEDIATE_PAYMENT(key = "IP");

    override fun toString(): String = key
}