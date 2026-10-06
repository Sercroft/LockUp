package com.sercroft.lockup.security

import android.content.Context

object LockMethodManager  {

    private const val PREFS = "lockup_prefs"
    private const val KEY_PIN_ENABLED = "pin_enabled"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"

    fun isPinEnabled(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_PIN_ENABLED, false)
    }

    fun isBiometricEnabled(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setPinEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_PIN_ENABLED, enabled)
            .apply()
    }

    fun setBiometricEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    fun hasAnyMethod(context: Context): Boolean {
        return isPinEnabled(context) || isBiometricEnabled(context)
    }
}