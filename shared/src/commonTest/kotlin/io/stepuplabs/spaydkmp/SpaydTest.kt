package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.Account
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SpaydTest {
    @Test
    fun minimalParameterSet() {
        val expected = "SPD*1.0*ACC:CZ7603000000000076327632"

        val spayd = Spayd(
            account = Account("CZ7603000000000076327632")
        )
        val actual = spayd.generate()

        assertEquals(expected, actual)
    }
}