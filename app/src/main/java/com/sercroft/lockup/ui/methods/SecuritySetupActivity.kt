package com.sercroft.lockup.ui.methods

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.sercroft.lockup.R
import com.sercroft.lockup.security.LockMethodManager
import com.sercroft.lockup.ui.main.MainActivity
import com.sercroft.lockup.ui.main.SetBiometricActivity
import com.sercroft.lockup.ui.main.SetPinActivity

class SecuritySetupActivity : AppCompatActivity() {

    private lateinit var switchPin: MaterialSwitch
    private lateinit var switchFingerprint: MaterialSwitch
    private lateinit var btnConfigurePin: ImageButton
    private lateinit var btnConfigureBiometric: ImageButton
    private lateinit var btnContinue: Button

    private var updatingSwitches = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_setup)

        switchPin = findViewById(R.id.switchPin)
        switchFingerprint = findViewById(R.id.switchFingerprint)
        btnConfigurePin = findViewById(R.id.btnConfigurePin)
        btnConfigureBiometric = findViewById(R.id.btnConfigureBiometric)
        btnContinue = findViewById(R.id.btnContinue)

        btnConfigurePin.visibility = View.GONE
        btnConfigureBiometric.visibility = View.GONE

        switchPin.setOnCheckedChangeListener { _, isChecked ->
            if (updatingSwitches) return@setOnCheckedChangeListener

            if (isChecked) {
                if (!LockMethodManager.isPinEnabled(this)) {
                    startActivity(Intent(this, SetPinActivity::class.java))
                }
            } else {
                if (!LockMethodManager.isBiometricEnabled(this)) {
                    setSwitchPin(true)
                    Toast.makeText(this, "Debes tener al menos un método de bloqueo activo", Toast.LENGTH_SHORT).show()
                    return@setOnCheckedChangeListener
                }

                LockMethodManager.setPinEnabled(this, false)
            }

            updateContinueButton()
        }

        switchFingerprint.setOnCheckedChangeListener { _, isChecked ->
            if (updatingSwitches) return@setOnCheckedChangeListener

            if (isChecked) {
                if (!LockMethodManager.isBiometricEnabled(this)) {
                    startActivity(Intent(this, SetBiometricActivity::class.java))
                }
            } else {
                if (!LockMethodManager.isPinEnabled(this)) {
                    setSwitchFingerprint(true)
                    Toast.makeText(this, "Debes tener al menos un método de bloqueo activo", Toast.LENGTH_SHORT).show()
                    return@setOnCheckedChangeListener
                }

                LockMethodManager.setBiometricEnabled(this, false)
            }

            updateContinueButton()
        }

        btnConfigurePin.setOnClickListener {
            startActivity(Intent(this, SetPinActivity::class.java))
        }

        btnConfigureBiometric.setOnClickListener {
            startActivity(Intent(this, SetBiometricActivity::class.java))
        }

        btnContinue.setOnClickListener {
            finishSetup()
        }

        updateSwitches()
        updateContinueButton()
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("lockup_prefs", MODE_PRIVATE)
        val setupBiometricAfterPin = prefs.getBoolean("setup_biometric_after_pin", false)

        if (setupBiometricAfterPin && LockMethodManager.isPinEnabled(this)) {
            prefs.edit()
                .putBoolean("setup_biometric_after_pin", false)
                .apply()

            if (!LockMethodManager.isBiometricEnabled(this)) {
                startActivity(Intent(this, SetBiometricActivity::class.java))
                return
            }
        }

        updateSwitches()
        updateContinueButton()
    }

    private fun updateSwitches() {
        updatingSwitches = true

        switchPin.isChecked = LockMethodManager.isPinEnabled(this)
        switchFingerprint.isChecked = LockMethodManager.isBiometricEnabled(this)

        updatingSwitches = false
    }

    private fun updateContinueButton() {
        val hasMethod = LockMethodManager.hasAnyMethod(this)

        btnContinue.isEnabled = hasMethod
        btnContinue.alpha = if (hasMethod) 1f else 0.5f
    }

    private fun setSwitchPin(checked: Boolean) {
        updatingSwitches = true
        switchPin.isChecked = checked
        updatingSwitches = false
    }

    private fun setSwitchFingerprint(checked: Boolean) {
        updatingSwitches = true
        switchFingerprint.isChecked = checked
        updatingSwitches = false
    }

    private fun finishSetup() {
        if (!LockMethodManager.hasAnyMethod(this)) {
            Toast.makeText(this, "Configura al menos un método de bloqueo", Toast.LENGTH_SHORT).show()
            return
        }

        getSharedPreferences("lockup_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_completed", true)
            .apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}