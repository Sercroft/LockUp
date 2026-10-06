package com.sercroft.lockup.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sercroft.lockup.R
import com.sercroft.lockup.security.BiometricHelper
import com.sercroft.lockup.security.LockMethodManager
import com.sercroft.lockup.security.PinManager


class SetBiometricActivity : AppCompatActivity() {

    private lateinit var pinContainer: TextInputLayout
    private lateinit var etPinCode: TextInputEditText
    private lateinit var btnEnableBiometric: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContentView(R.layout.activity_set_biometric)

        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }

        val root = findViewById<View>(R.id.rootLayout)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )

            insets
        }

        pinContainer = findViewById(R.id.pinContainer)
        etPinCode = findViewById(R.id.etPinCode)
        btnEnableBiometric = findViewById(R.id.btnEnableBiometric)

        val pinEnabled = LockMethodManager.isPinEnabled(this)

        if (pinEnabled) {
            pinContainer.visibility = View.VISIBLE
        } else {
            pinContainer.visibility = View.GONE
        }

        btnEnableBiometric.setOnClickListener {

            if (!pinEnabled) {
                authenticateBiometric()
                return@setOnClickListener
            }

            val pinCode = etPinCode.text.toString()

            if (pinCode.isEmpty()) {
                etPinCode.error = "Ingresa tu PIN"
                return@setOnClickListener
            }

            if (!PinManager.verifyPinCode(this, pinCode)) {
                etPinCode.error = "PIN incorrecto"
                return@setOnClickListener
            }

            authenticateBiometric()
        }
    }

    private fun authenticateBiometric() {
        BiometricHelper.authenticate(this) {
            LockMethodManager.setBiometricEnabled(this, true)
            Toast.makeText(this, "Huella dactilar activada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}