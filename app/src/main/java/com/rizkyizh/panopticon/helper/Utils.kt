package com.rizkyizh.panopticon.helper

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AlertDialog

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun simpleAlertDialog(context: Context, toPreviousActivity: Boolean, title: String, message: String){

    if (context is Activity && !context.isFinishing) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("oke") { _, _ ->
                if (toPreviousActivity) {
                    context.finish()
                }
            }
            create()
            show()
        }
    }

}