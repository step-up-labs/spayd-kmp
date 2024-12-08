package io.stepuplabs.spaydkmp.common

data class Account(
    val iban: String,
    val bic: String? = null,
) {
    override fun toString(): String = if (bic == null) {
        iban
    } else {
        "$iban+$bic"
    }
}