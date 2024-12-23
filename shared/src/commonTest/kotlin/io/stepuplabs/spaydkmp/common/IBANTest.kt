package io.stepuplabs.spaydkmp.common

import io.stepuplabs.spaydkmp.exception.ValidationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class IBANTest {
    private val iban = IBAN()

    @Test
    fun czechAccount() {
        assertEquals(
            iban.createForCzechAccount(null, account = 76327632, bank = 300),
            "CZ7603000000000076327632",
        )
    }
}