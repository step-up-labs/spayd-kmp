package io.stepuplabs.spaydkmp.common

/*
Notification type representation
 */
@Suppress("UNUSED")
enum class NotificationType(val key: String) {
    EMAIL(key = "E"),
    PHONE(key = "P");

    override fun toString(): String = key
}