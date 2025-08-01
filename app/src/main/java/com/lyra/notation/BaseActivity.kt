package com.lyra.notation

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

abstract class BaseActivity(@LayoutRes private val layoutRes: Int): AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(layoutRes)
    }

    fun isPortrait(): Boolean {
        val metrics = resources.displayMetrics
        return metrics.heightPixels > metrics.widthPixels
    }
}