package io.stepuplabs.spaydkmp.common

@Suppress("UNUSED")
enum class NotificationType(val key: String) {
    EMAIL(key = "E"),
    PHONE(key = "P");

    override fun toString(): String = key
}