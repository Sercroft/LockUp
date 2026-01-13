package com.sercroft.lockup.security

object UnlockSessionManager {

    private var lastUnlockTime: Long = 0
    private var unlockedPackage: String? = null

    fun unlock(pkgName: String) {
        unlockedPackage = pkgName
        lastUnlockTime = System.currentTimeMillis()
    }

    fun recentlyUnlocked(): Boolean {
        return System.currentTimeMillis() - lastUnlockTime < 800
    }

    fun isUnlocked(pkgName: String): Boolean {
        return unlockedPackage == pkgName
    }

    fun clearIfAppChanged(newPkg: String) {
        if (unlockedPackage != null && unlockedPackage != newPkg) {
            unlockedPackage = null
        }
    }

    fun clearAll() {
        unlockedPackage = null
    }
}