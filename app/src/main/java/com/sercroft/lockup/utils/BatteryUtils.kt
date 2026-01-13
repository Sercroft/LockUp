package com.sercroft.lockup.utils

import android.content.Context
import android.os.PowerManager

object BatteryUtils {
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}