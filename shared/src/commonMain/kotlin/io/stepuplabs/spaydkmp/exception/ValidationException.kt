package io.stepuplabs.spaydkmp.exception

/*
Exception that represents failed parameter validation effort
 */
class ValidationException(
    override val message: String?,
): Throwable()