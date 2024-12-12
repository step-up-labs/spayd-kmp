package io.stepuplabs.spaydkmp.common

/*
Account representation
 */
data class Account(
    val iban: String,
    val bic: String? = null,
) {
    @Suppress("UNUSED")
    constructor(
        prefix: Long?,
        account: Long,
        bank: Long,
    ) : this(iban = IBAN().createForCzechAccount(prefix, account, bank))

    override fun toString(): String = if (bic == null) {
        iban
    } else {
        "$iban+$bic"
    }
}