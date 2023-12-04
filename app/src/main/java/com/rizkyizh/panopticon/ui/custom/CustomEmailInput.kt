package com.rizkyizh.panopticon.ui.custom

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class CustomEmailTextInput: TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        hint = "Email"
        textSize = 16f
        error = null

    }

}