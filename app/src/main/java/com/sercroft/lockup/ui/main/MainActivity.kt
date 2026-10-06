package com.sercroft.lockup.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.sercroft.lockup.R
import com.sercroft.lockup.security.LockMethodManager
import com.sercroft.lockup.ui.apps.AppsActivity
import com.sercroft.lockup.utils.AccessibilityUtils
import com.sercroft.lockup.utils.BatteryUtils

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private lateinit var securityContainer: View
    private lateinit var tvAccessibilityStatus: TextView

    private lateinit var btnAccessibility: Button
    private lateinit var btnConfigurePin: ImageButton
    private lateinit var btnConfigureBiometric: ImageButton
    private lateinit var btnManageApps: Button

    private lateinit var switchPin: MaterialSwitch
    private lateinit var switchFingerprint: MaterialSwitch

    private var updatingSwitches = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("lockup_prefs", MODE_PRIVATE)

        securityContainer = findViewById(R.id.securityContainer)
        tvAccessibilityStatus = findViewById(R.id.tvAccessibilityStatus)

        btnAccessibility = findViewById(R.id.btnAccessibility)
        btnConfigurePin = findViewById(R.id.btnConfigurePin)
        btnConfigureBiometric = findViewById(R.id.btnConfigureBiometric)
        btnManageApps = findViewById(R.id.btnManageApps)

        switchPin = findViewById(R.id.switchPin)
        switchFingerprint = findViewById(R.id.switchFingerprint)

        btnAccessibility.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }

        btnConfigurePin.setOnClickListener {
            startActivity(Intent(this, SetPinActivity::class.java))
        }

        btnConfigureBiometric.setOnClickListener {
            startActivity(Intent(this, SetBiometricActivity::class.java))
        }

        switchPin.setOnCheckedChangeListener { _, isChecked ->
            if (updatingSwitches) return@setOnCheckedChangeListener

            if (isChecked) {
                if (LockMethodManager.isPinEnabled(this)) {
                    return@setOnCheckedChangeListener
                }

                startActivity(Intent(this, SetPinActivity::class.java))
                setSwitchPin(false)
            } else {
                if (!LockMethodManager.isBiometricEnabled(this)) {
                    setSwitchPin(true)
                    Toast.makeText(this, "No puedes desactivar el PIN porque es tu único método de bloqueo", Toast.LENGTH_SHORT).show()
                    return@setOnCheckedChangeListener
                }

                disablePin()
            }
        }

        switchFingerprint.setOnCheckedChangeListener { _, isChecked ->
            if (updatingSwitches) return@setOnCheckedChangeListener

            if (isChecked) {
                if (LockMethodManager.isBiometricEnabled(this)) {
                    return@setOnCheckedChangeListener
                }

                startActivity(Intent(this, SetBiometricActivity::class.java))
                setSwitchFingerprint(false)
            } else {
                if (!LockMethodManager.isPinEnabled(this)) {
                    setSwitchFingerprint(true)
                    Toast.makeText(this, "No puedes desactivar la huella porque es tu único método de bloqueo", Toast.LENGTH_SHORT).show()
                    return@setOnCheckedChangeListener
                }

                disableBiometric()
            }
        }

        btnManageApps.setOnClickListener {
            startActivity(Intent(this, AppsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        val accessibilityEnabled = AccessibilityUtils.isAccessibilityServiceEnabled(this)
        val batteryOk = BatteryUtils.isIgnoringBatteryOptimizations(this)
        val hasSecurityMethod = LockMethodManager.hasAnyMethod(this)

        if (accessibilityEnabled) {
            tvAccessibilityStatus.text = "Accesibilidad ACTIVADA"
            tvAccessibilityStatus.setTextColor(Color.GREEN)

            btnAccessibility.visibility = View.GONE
            securityContainer.visibility = View.VISIBLE

            updateSecuritySwitches()

            if (hasSecurityMethod) {
                btnManageApps.visibility = View.VISIBLE
            } else {
                btnManageApps.visibility = View.GONE
            }

            if (!batteryOk && !prefs.getBoolean("battery_warned", false)) {
                showBatteryWarningDialog()
                prefs.edit().putBoolean("battery_warned", true).apply()
            }

        } else {
            tvAccessibilityStatus.text = "Accesibilidad DESACTIVADA"
            tvAccessibilityStatus.setTextColor(Color.RED)

            btnAccessibility.visibility = View.VISIBLE
            securityContainer.visibility = View.GONE
            btnManageApps.visibility = View.GONE
        }
    }

    private fun updateSecuritySwitches() {
        updatingSwitches = true

        switchPin.isChecked = LockMethodManager.isPinEnabled(this)
        switchFingerprint.isChecked = LockMethodManager.isBiometricEnabled(this)

        updatingSwitches = false
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

    private fun disablePin() {
        if (!LockMethodManager.isBiometricEnabled(this)) {
            setSwitchPin(true)
            Toast.makeText(this, "No puedes desactivar el PIN porque es tu único método de bloqueo", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Desactivar PIN")
            .setMessage("¿Seguro que quieres desactivar el PIN?\n\nLa huella dactilar seguirá activa.")
            .setNegativeButton("Cancelar") { _, _ ->
                setSwitchPin(true)
            }
            .setPositiveButton("Desactivar") { _, _ ->
                LockMethodManager.setPinEnabled(this, false)
                Toast.makeText(this, "PIN desactivado", Toast.LENGTH_SHORT).show()
                updateSecuritySwitches()
            }
            .show()
    }

    private fun disableBiometric() {
        if (!LockMethodManager.isPinEnabled(this)) {
            setSwitchFingerprint(true)
            Toast.makeText(this, "No puedes desactivar la huella porque es tu único método de bloqueo", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Desactivar huella")
            .setMessage("¿Seguro que quieres desactivar la huella dactilar?\n\nEl PIN seguirá activo.")
            .setNegativeButton("Cancelar") { _, _ ->
                setSwitchFingerprint(true)
            }
            .setPositiveButton("Desactivar") { _, _ ->
                LockMethodManager.setBiometricEnabled(this, false)
                Toast.makeText(this, "Huella dactilar desactivada", Toast.LENGTH_SHORT).show()
                updateSecuritySwitches()
            }
            .show()
    }

    private fun showBatteryWarningDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso adicional requerido")
            .setMessage(
                "Para que Lock Up funcione correctamente, debes desactivar " +
                        "la optimización de batería.\n\n" +
                        "Ruta:\nAjustes → Batería → Lock Up → Sin restricciones"
            )
            .setCancelable(false)
            .setPositiveButton("Abrir ajustes") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    startActivity(intent)
                } catch (e: Exception) {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }
            .setNegativeButton("Más tarde", null)
            .show()
    }
}