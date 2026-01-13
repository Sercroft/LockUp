package com.sercroft.lockup.utils


import android.content.Context
import android.provider.Settings

object AccessibilityUtils {

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        return enabledServices.contains(context.packageName)
    }
}