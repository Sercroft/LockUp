package com.sercroft.lockup.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast

object AppLauncherUtils {
    fun launchApp(context: Context, pkgName: String, className: String) {
        try {
            val intent = Intent().apply {
                component = ComponentName(pkgName, className)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            val fallback = context.packageManager.getLaunchIntentForPackage(pkgName)

            if (fallback != null) {
                fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallback)
            } else {
                Toast.makeText(context, "No se pudo abrir la app", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


