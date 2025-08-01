package com.lyra.notation.extension

import android.graphics.Rect
import android.view.View

fun getViewRect(view: View): Rect {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    return Rect(
        location[0],
        location[1],
        location[0] + view.width,
        location[1] + view.height
    )
}