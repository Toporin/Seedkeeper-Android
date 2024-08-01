package org.satochip.seedkeeper.data

enum class AuthenticityStatus {
    AUTHENTIC,
    NOT_AUTHENTIC,
    UNKNOWN, // default when no card has been scanned yet
}