package io.stepuplabs.spaydkmp.common

/*
Representation of multiple Accounts
 */
data class BankAccountList(
    val bankAccounts: List<BankAccount>
) {
    override fun toString(): String {
        val builder = StringBuilder()
        for (account in bankAccounts) {
            if (builder.isNotEmpty()) {
                builder.append(",")
            }

            builder.append(account.toString())
        }

        return builder.toString()
    }
}
