package com.sercroft.lockup.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sercroft.lockup.R
import com.sercroft.lockup.security.LockMethodManager
import com.sercroft.lockup.ui.methods.SecuritySetupActivity
import com.sercroft.lockup.utils.AccessibilityUtils

class OnboardingActivity : AppCompatActivity() {

    private lateinit var tvStep: TextView
    private lateinit var imgStep: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnAction: Button
    private lateinit var btnNext: Button

    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        tvStep = findViewById(R.id.tvStep)
        imgStep = findViewById(R.id.imgStep)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        btnAction = findViewById(R.id.btnAction)
        btnNext = findViewById(R.id.btnNext)

        showStepOne()

        btnAction.setOnClickListener {
            if (currentStep == 1) {
                openAccessibilitySettings()
            } else {
                openSecuritySetup()
            }
        }

        btnNext.setOnClickListener {
            if (currentStep == 1) {
                if (!AccessibilityUtils.isAccessibilityServiceEnabled(this)) {
                    return@setOnClickListener
                }

                currentStep = 2
                showStepTwo()
            } else {
                if (!LockMethodManager.hasAnyMethod(this)) {
                    return@setOnClickListener
                }

                finishOnboarding()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (currentStep == 1 && AccessibilityUtils.isAccessibilityServiceEnabled(this)) {
            btnNext.isEnabled = true
            btnNext.alpha = 1f
            btnAction.text = "Accesibilidad activada"
        }
    }

    private fun showStepOne() {
        currentStep = 1

        tvStep.text = "Paso 1 de 2"
        tvTitle.text = "Activa la accesibilidad"
        tvDescription.text =
            "Lock Up necesita el servicio de accesibilidad para detectar cuándo abres una aplicación protegida y mostrar la pantalla de bloqueo."

        btnAction.text = "Activar accesibilidad"
        btnNext.text = "Continuar"
        btnNext.isEnabled = AccessibilityUtils.isAccessibilityServiceEnabled(this)
        btnNext.alpha = if (btnNext.isEnabled) 1f else 0.5f
    }

    private fun showStepTwo() {
        currentStep = 2

        tvStep.text = "Paso 2 de 2"
        tvTitle.text = "Configura tu método de bloqueo"
        tvDescription.text =
            "Elige cómo quieres proteger tus aplicaciones. Puedes utilizar solamente un PIN, solamente la huella dactilar o ambos métodos."

        btnAction.text = "Configurar método"
        btnNext.text = "Finalizar"
        btnNext.isEnabled = LockMethodManager.hasAnyMethod(this)
        btnNext.alpha = if (btnNext.isEnabled) 1f else 0.5f
    }

    private fun openAccessibilitySettings() {
        try {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun openSecuritySetup() {
        startActivity(Intent(this, SecuritySetupActivity::class.java))
    }

    private fun finishOnboarding() {
        getSharedPreferences("lockup_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_completed", true)
            .apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}