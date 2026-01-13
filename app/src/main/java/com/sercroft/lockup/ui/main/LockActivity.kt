package com.sercroft.lockup.ui.main

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sercroft.lockup.R
import com.sercroft.lockup.security.BiometricHelper
import com.sercroft.lockup.security.PinManager
import com.sercroft.lockup.security.UnlockSessionManager
import com.sercroft.lockup.utils.AppLauncherUtils
import com.sercroft.lockup.utils.Constants

class LockActivity : AppCompatActivity() {

    private lateinit var targetPkg: String
    private lateinit var targetClass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        targetPkg = intent.getStringExtra(Constants.EXTRA_LOCK_PKG) ?: run {
            finish(); return
        }
        targetClass = intent.getStringExtra(Constants.EXTRA_LOCK_CLASS) ?: run {
            finish(); return
        }

        val etPinCode = findViewById<EditText>(R.id.etPinCode)
        val btnUnlock = findViewById<Button>(R.id.btnUnlock)
        val btnBiometric = findViewById<Button>(R.id.btnBiometric)

        btnUnlock.setOnClickListener {
            val pinCode = etPinCode.text.toString()
            if (PinManager.verifyPinCode(this, pinCode)) {
                UnlockSessionManager.unlock(targetPkg)
                AppLauncherUtils.launchApp(this, targetPkg, targetClass)
                finish()
            } else {
                Toast.makeText(this, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
            }
        }

        btnBiometric.setOnClickListener {
            BiometricHelper.authenticate(this) {
                UnlockSessionManager.unlock(targetPkg)
                AppLauncherUtils.launchApp(this, targetPkg, targetClass)
                finish()
            }
        }
    }
}

