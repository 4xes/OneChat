package com.android.kit.extensions

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast

fun Context?.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    this?.apply {
        Toast.makeText(this, message, duration).show()
    }
}

fun Context?.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    this?.apply {
        toast(getString(message, duration))
    }
}

fun View?.snackBar(message: String, action:String? = null, listener: View.OnClickListener? = null, duration: Int = Snackbar.LENGTH_SHORT) {
    if (this != null){
        val snackbar = Snackbar.make(this, message, duration)
        if (action != null) {
            snackbar.setAction(action, listener)
        }
        snackbar.show()
    }
}
