package com.sercroft.lockup.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.sercroft.lockup.R
import com.sercroft.lockup.security.BiometricHelper
import com.sercroft.lockup.security.LockMethodManager
import com.sercroft.lockup.security.PinManager

class SetPinActivity : AppCompatActivity() {

    private lateinit var etPin: EditText
    private lateinit var etConfirmPin: EditText
    private lateinit var btnSavePin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContentView(R.layout.activity_set_pincode)

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

        etPin = findViewById(R.id.etPin)
        etConfirmPin = findViewById(R.id.etConfirmPin)
        btnSavePin = findViewById(R.id.btnSavePin)

        btnSavePin.setOnClickListener {

            val pinEnabled = LockMethodManager.isPinEnabled(this)
            val biometricEnabled = LockMethodManager.isBiometricEnabled(this)

            if (!pinEnabled && biometricEnabled) {
                BiometricHelper.authenticate(this) {
                    savePin()
                }
                return@setOnClickListener
            }

            savePin()
        }
    }

    private fun savePin() {

        val pin = etPin.text.toString()
        val confirmPin = etConfirmPin.text.toString()

        if (pin.length < 4) {
            Toast.makeText(this, "El PIN debe tener al menos 4 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        if (pin != confirmPin) {
            Toast.makeText(this, "Los PIN no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        PinManager.savePinCode(this, pin)
        LockMethodManager.setPinEnabled(this, true)
        Toast.makeText(this, "PIN configurado correctamente", Toast.LENGTH_SHORT).show()

        finish()
    }
}