package com.android.kit.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Long.toDuration(): String {
    val date = Date()
    val formatter = SimpleDateFormat(if (this >= 3600L) "HH:mm:ss" else "mm:ss")
    return formatter.format(date)
}
