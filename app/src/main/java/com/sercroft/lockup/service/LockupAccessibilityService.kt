package com.sercroft.lockup.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.sercroft.lockup.data.repository.BlockedAppsRepository
import com.sercroft.lockup.security.UnlockSessionManager
import com.sercroft.lockup.ui.main.LockActivity
import com.sercroft.lockup.utils.Constants

class LockupAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkgName = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: return

        if (pkgName == this.packageName) return

        if (UnlockSessionManager.recentlyUnlocked()) return
        UnlockSessionManager.clearIfAppChanged(pkgName)

        if (BlockedAppsRepository.isBlocked(pkgName) && !UnlockSessionManager.isUnlocked(pkgName)) {
            launchLockScreen(pkgName, className)
        }
    }

    private fun launchLockScreen(pkgName: String, className: String) {
        val intent = Intent(this, LockActivity::class.java).apply {
            putExtra(Constants.EXTRA_LOCK_PKG, pkgName)
            putExtra(Constants.EXTRA_LOCK_CLASS, className)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )
        }
        startActivity(intent)
    }

    override fun onInterrupt() {}
}
