package com.juntoscontracorona.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper

inline fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false, theme: Int? = null): View {
    val contextTheme = if (theme != null)
        ContextThemeWrapper(context, theme)
    else
        context

    return LayoutInflater.from(contextTheme).inflate(layoutId, this, attachToRoot)
}