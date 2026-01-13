package com.sercroft.lockup.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.sercroft.lockup.R
import com.sercroft.lockup.security.PinManager
import com.sercroft.lockup.utils.AccessibilityUtils
import com.sercroft.lockup.utils.BatteryUtils

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    private lateinit var btnAccessibility: Button
    private lateinit var btnSetPinCode: Button
    private lateinit var tvAccessibilityStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("lockup_prefs", MODE_PRIVATE)

        PinManager.savePinCode(this, "1234")

        btnAccessibility = findViewById(R.id.btnAccessibility)
        btnSetPinCode = findViewById(R.id.btnSetPinCode)
        tvAccessibilityStatus = findViewById(R.id.tvAccessibilityStatus)

        btnAccessibility.setOnClickListener {
            try {
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }

        btnSetPinCode.setOnClickListener {
            val intent = Intent(this, LockActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val accessibilityEnabled = AccessibilityUtils.isAccessibilityServiceEnabled(this)
        val batteryOk = BatteryUtils.isIgnoringBatteryOptimizations(this)

        if (accessibilityEnabled) {
            tvAccessibilityStatus.text = "Accesibilidad ACTIVADA"
            tvAccessibilityStatus.setTextColor(Color.GREEN)
            btnAccessibility.visibility = View.GONE

            if (!batteryOk && !prefs.getBoolean("battery_warned", false)) {
                showBatteryWarningDialog()
                prefs.edit().putBoolean("battery_warned", true).apply()
            }
        } else {
            tvAccessibilityStatus.text = "Accesibilidad DESACTIVADA"
            tvAccessibilityStatus.setTextColor(Color.RED)
            btnAccessibility.visibility = View.VISIBLE
        }
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