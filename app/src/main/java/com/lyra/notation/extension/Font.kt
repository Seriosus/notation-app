package com.lyra.notation.extension

import android.content.Context
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import androidx.core.content.res.ResourcesCompat
import com.lyra.notation.R

fun getOpenSansFont(context: Context): Typeface? {
    return ResourcesCompat.getFont(context, R.font.opensans)
}

fun getFiraCodeFont(context: Context): Typeface? {
    return ResourcesCompat.getFont(context, R.font.firacode)
}

class CustomTypefaceSpan(private val customTypeface: Typeface?) : MetricAffectingSpan() {
    override fun updateMeasureState(paint: TextPaint) {
        apply(paint)
    }

    override fun updateDrawState(tp: TextPaint) {
        apply(tp)
    }

    private fun apply(paint: TextPaint) {
        paint.typeface = customTypeface
    }
}
