package com.lyra.notation.extension

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun <T> MutableList<T>.findAndKill(predicate: (T) -> Boolean): Boolean {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        if (predicate(iterator.next())) {
            iterator.remove()
            return true
        }
    }
    return false
}

fun Rect.withToleranceShrink(tolerance: Int): Rect {
    val widthShrink = tolerance * 2
    val heightShrink = tolerance * 2

    // Evitamos que el rect se invierta (porque si se pasa, se vuelve basura)
    val newWidth = (this.width() - widthShrink).coerceAtLeast(1)
    val newHeight = (this.height() - heightShrink).coerceAtLeast(1)

    val centerX = this.centerX()
    val centerY = this.centerY()

    return Rect(
        centerX - newWidth / 2,
        centerY - newHeight / 2,
        centerX + newWidth / 2,
        centerY + newHeight / 2
    )
}

fun Rect.withToleranceGrow(tolerance: Int): Rect {
    val centerX = this.centerX()
    val centerY = this.centerY()

    return Rect(
        centerX - this.width() / 2 - tolerance,
        centerX + this.width() / 2 + tolerance,
        centerY - this.height() / 2 - tolerance,
        centerY + this.height() / 2 + tolerance,
    )
}

fun Context.isInLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

fun RecyclerView.adjustGridSpan(portraitSpan: Int = 2, landscapeSpan: Int = 4) {
    val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val newSpan = if (isLandscape) landscapeSpan else portraitSpan
    (layoutManager as? GridLayoutManager)?.spanCount = newSpan
}