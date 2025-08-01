package com.lyra.notation.model

import android.text.Spanned

data class Note (
    val noteItemId: String,
    val body: String,
    val createdAt: Long,
    val lastUpdatedAt: Long
)

data class PartialNote (
    var body: String?
)