package io.stepuplabs.spaydkmp

import io.stepuplabs.spaydkmp.common.Account
import io.stepuplabs.spaydkmp.common.NotificationType
import io.stepuplabs.spaydkmp.exception.ValidationException
import io.stepuplabs.spaydkmp.value.Kind
import io.stepuplabs.spaydkmp.value.Value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class SpaydTest {
    @Test
    fun notEnoughParameters() {
        assertFailsWith(ValidationException::class) {
            Spayd().generate()
        }
    }

    @Test
    fun minimalParameterSet() {
        val expected = "SPD*1.0*ACC:CZ7603000000000076327632"

        val spayd = Spayd(
            Value(kind = Kind.ACCOUNT, value = Account("CZ7603000000000076327632"))
        )
        val actual = spayd.generate()

        assertEquals(expected, actual)
    }

    @Test
    fun fullParameterSet() {
        val expected = "SPD*1.0*ACC:CZ7603000000000076327632*NT:P*NTA:+420321654987"

        val spayd = Spayd(
            Value(kind = Kind.ACCOUNT, value = Account("CZ7603000000000076327632")),
            Value(kind = Kind.NOTIFY_TYPE, value = NotificationType.PHONE),
            Value(kind = Kind.NOTIFY_ADDRESS, value = "+420321654987"),

            // TODO: fill the rest
        )
        val actual = spayd.generate()

        assertEquals(expected, actual)
    }
}