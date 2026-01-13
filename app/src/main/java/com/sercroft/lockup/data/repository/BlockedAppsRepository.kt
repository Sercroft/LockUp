package com.sercroft.lockup.data.repository

object BlockedAppsRepository {

    private val blockedApps = mutableSetOf(
        "com.whatsapp",
        "com.android.chrome"
    )

    fun isBlocked(pkgName: String): Boolean {
        return blockedApps.contains(pkgName)
    }

    fun blockApp(pkgName: String) {
        blockedApps.add(pkgName)
    }

    fun unblockApp(pkgName: String) {
        blockedApps.remove(pkgName)
    }

    fun getBlockedApps(): Set<String> = blockedApps
}