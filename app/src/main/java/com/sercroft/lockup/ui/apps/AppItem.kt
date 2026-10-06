package com.sercroft.lockup.ui.apps

import android.graphics.drawable.Drawable

data class AppItem(
    val name: String,
    val pkgName: String,
    val icon: Drawable,
    var isBlocked: Boolean
)

