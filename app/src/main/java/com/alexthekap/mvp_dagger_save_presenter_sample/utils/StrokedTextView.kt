package com.alexthekap.mvp_dagger_save_presenter_sample.utils

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView;

class StrokedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    override fun onDraw(canvas: Canvas?) {
        for (i in 0..5) {
            super.onDraw(canvas)
        }
    }
}