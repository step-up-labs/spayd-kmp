package io.stepuplabs.spaydkmp.common

/*
Account representation
 */
data class BankAccount(
    val iban: String,
    val bic: String? = null,
) {
    override fun toString(): String = if (bic == null) {
        iban
    } else {
        "$iban+$bic"
    }
}