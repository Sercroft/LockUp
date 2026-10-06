package com.sercroft.lockup.ui.main

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.sercroft.lockup.R
import com.sercroft.lockup.security.BiometricHelper
import com.sercroft.lockup.security.LockMethodManager
import com.sercroft.lockup.security.PinManager
import com.sercroft.lockup.security.UnlockSessionManager
import com.sercroft.lockup.utils.AppLauncherUtils
import com.sercroft.lockup.utils.Constants

class LockActivity : AppCompatActivity() {

    private lateinit var targetPkg: String
    private lateinit var targetClass: String

    private lateinit var pinContainer: TextInputLayout
    private lateinit var etPinCode: EditText
    private lateinit var btnUnlock: Button
    private lateinit var btnBiometric: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        targetPkg = intent.getStringExtra(Constants.EXTRA_LOCK_PKG) ?: run {
            finish()
            return
        }

        targetClass = intent.getStringExtra(Constants.EXTRA_LOCK_CLASS) ?: run {
            finish()
            return
        }

        pinContainer = findViewById(R.id.pinContainer)
        etPinCode = findViewById(R.id.etPinCode)
        btnUnlock = findViewById(R.id.btnUnlock)
        btnBiometric = findViewById(R.id.btnBiometric)

        val pinEnabled = LockMethodManager.isPinEnabled(this)
        val biometricEnabled =  LockMethodManager.isBiometricEnabled(this)

        if (pinEnabled) {
            pinContainer.visibility = View.VISIBLE
            btnUnlock.visibility = View.VISIBLE
        } else {
            pinContainer.visibility = View.GONE
            btnUnlock.visibility = View.GONE
        }

        if (biometricEnabled) {
            btnBiometric.visibility = View.VISIBLE
        } else {
            btnBiometric.visibility = View.GONE
        }

        btnUnlock.setOnClickListener {

            val pinCode = etPinCode.text.toString()

            if (PinManager.verifyPinCode(this, pinCode)) {
                unlockApp()
            } else {
                Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            }
        }

        btnBiometric.setOnClickListener {
            BiometricHelper.authenticate(this) {
                unlockApp()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    private fun unlockApp() {
        UnlockSessionManager.unlock(targetPkg)
        AppLauncherUtils.launchApp( this, targetPkg, targetClass)
        finish()
    }
}