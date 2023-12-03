package com.rizkyizh.panopticon.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.rizkyizh.panopticon.R

class MainButton: MaterialButton {
    private var gradientDrawable: GradientDrawable = GradientDrawable()
    private var startColor: Int = 0
    private var endColor: Int = 0
    private var bgTintColor: Int = 0
    private var bgTintColorDisable: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backgroundTintList =
            if (isEnabled) ColorStateList.valueOf(bgTintColor) else ColorStateList.valueOf(
                bgTintColorDisable
            )




    }

    private fun init() {
//        bgTintColorDisable = ContextCompat.getColor(context, R.color.button_disable)
        bgTintColor = ContextCompat.getColor(context, R.color.white)


        startColor = ContextCompat.getColor(context, R.color.md_theme_light_primary_40)
        endColor = ContextCompat.getColor(context, R.color.md_theme_light_primary_90)
//        bgTintColorDisable = ContextCompat.getColor(context, R.color.button_disable)

        // Set initial gradient
        updateGradient()

        // Set default text color
        setTextColor(ContextCompat.getColor(context, R.color.white))

    }

    private fun updateGradient() {
        gradientDrawable.colors = intArrayOf(startColor, endColor)
        gradientDrawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
    }
//
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        if (isEnabled) {
//            when (event?.action) {
//                MotionEvent.ACTION_DOWN -> {
////                    bgTintColor = ContextCompat.getColor(context, R.color.primer)
//                    setTextColor(ContextCompat.getColor(context, R.color.white))
//                }
//
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    bgTintColor = ContextCompat.getColor(context, R.color.white)
////                    setTextColor(ContextCompat.getColor(context, R.color.primer))
//                }
//            }
//        }
//        return super.onTouchEvent(event)

//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Set pressed state color
//                    startColor = ContextCompat.getColor(context, R.color.start_color_pressed)
//                    endColor = ContextCompat.getColor(context, R.color.end_color_pressed)
//                    updateGradient()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Set normal state color
//                    startColor = ContextCompat.getColor(context, R.color.start_color)
//                    endColor = ContextCompat.getColor(context, R.color.end_color)
//                    updateGradient()
                }
            }
        }
        return super.onTouchEvent(event)
    }
}