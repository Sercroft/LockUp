package com.sercroft.lockup.security

import android.content.Context
import java.security.MessageDigest

object PinManager {

    private const val PREFS = "lockup_prefs"
    private const val KEY_PIN_CODE = "pin_code_hash"

    fun savePinCode(context: Context, pinCode: String) {
        val hash = hash(pinCode)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PIN_CODE, hash)
            .apply()
    }

    fun verifyPinCode(context: Context, pinCode: String): Boolean {
        val storedHash = context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_PIN_CODE, null)

        return storedHash != null && storedHash == hash(pinCode)
    }

    private fun hash(text: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(text.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) }
    }
}