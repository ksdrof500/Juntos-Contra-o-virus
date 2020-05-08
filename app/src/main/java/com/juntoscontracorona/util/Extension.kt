package com.juntoscontracorona.util

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.juntoscontracorona.R
import kotlinx.android.synthetic.main.custom_toast_layout.view.*
import kotlinx.android.synthetic.main.custom_toast_success_layout.view.*
import java.text.DecimalFormat

val dec = DecimalFormat("##.##")

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.toastError(message: CharSequence) {
    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = inflater.inflate(R.layout.custom_toast_layout, null)

    layout.message.text = message

    Toast(this).apply {
        setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 20.dpToPx())
        duration = Toast.LENGTH_LONG
        view = layout
        show()
    }
}

fun Context.toastSuccess(message: CharSequence) {
    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = inflater.inflate(R.layout.custom_toast_success_layout, null)

    layout.txtTitle.text = message

    Toast(this).apply {
        setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, 60.dpToPx())
        duration = Toast.LENGTH_LONG
        view = layout
        show()
    }
}