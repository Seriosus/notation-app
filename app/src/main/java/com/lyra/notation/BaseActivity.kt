package com.lyra.notation

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

abstract class BaseActivity(@LayoutRes private val layoutRes: Int): AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(layoutRes)
    }

    private fun setupInsetSpacer() {
        val spacer = findViewById<View?>(R.id.statusBarSpacer) ?: return

        ViewCompat.setOnApplyWindowInsetsListener(spacer) {
            view, insets ->
                val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
                view.updateLayoutParams {
                    height = top
                }
                insets
        }
    }

    fun isPortrait(): Boolean {
        val metrics = resources.displayMetrics
        return metrics.heightPixels > metrics.widthPixels
    }
}