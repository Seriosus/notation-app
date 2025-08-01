package com.lyra.notation.model

data class NoteItem (
    val id: String,
    val title: String,
    val description: String,
    val backgroundColor: Int,
    val orderId: Int
)

data class PartialNoteItem (
    var title: String?,
    var description: String?,
    var backgroundColor: Int?,
    var orderId: Int?
)