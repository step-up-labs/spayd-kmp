package io.stepuplabs.spaydkmp.common

data class Notification(
    val type: NotificationType,
    val address: String,
)