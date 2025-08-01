package com.lyra.notation

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.lyra.notation.extension.getFiraCodeFont
import com.lyra.notation.extension.getOpenSansFont

class EditorActivity: BaseActivity(R.layout.activity_editor) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun showMessageNoteNotFoundAndFinish() {
        Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showMessageNoteUpdatedAndFinish() {
        Toast.makeText(this, "Note updated ✅", Toast.LENGTH_SHORT).show()
        finish() // Vuelve a la preview
    }
    private fun init() {
        val noteItemId = intent.getStringExtra("NOTE_ITEM_ID") ?: return finish()
        val note = NoteStorage.getNoteByNoteItemId(noteItemId) ?: return showMessageNoteNotFoundAndFinish()

        val noteTitleTextInput = findViewById<EditText>(R.id.editorNoteTitle)
        val noteDescriptionTextInput = findViewById<EditText>(R.id.editorNoteDescription)

        val noteItem = NoteStorage.getNoteItems().find { noteItem -> noteItem.id == noteItemId } ?: return showMessageNoteNotFoundAndFinish()
        noteTitleTextInput.setText(noteItem.title)
        noteDescriptionTextInput.setText(noteItem.description)

        val bodyInput = findViewById<EditText>(R.id.editorBodyInput)
        bodyInput.typeface = getFiraCodeFont(this)
        val saveButton = findViewById<Button>(R.id.saveNoteButton)

        bodyInput.setText(note.body)

        saveButton.setOnClickListener {
            val updatedBody = bodyInput.text.toString()
            val updatedTitle = noteTitleTextInput.text.toString()
            val updatedDescription = noteDescriptionTextInput.text.toString()

            if (updatedTitle.isEmpty()) {
                noteTitleTextInput.error = "Note must have a title ❌"
                return@setOnClickListener
            }

            val updatedNote = note.copy(
                body = updatedBody,
                lastUpdatedAt = System.currentTimeMillis()
            )

            val updatedNoteItem = noteItem.copy(
                title = updatedTitle,
                description = updatedDescription
            )

            val updatedNotes = NoteStorage.getNotes().map {
                if (it.noteItemId == noteItemId) updatedNote else it
            }

            val updatedNoteItems = NoteStorage.getNoteItems().map {
                if (it.id == noteItemId) updatedNoteItem else it
            }

            NoteStorage.updateFiles(updatedNoteItems, updatedNotes)
            showMessageNoteUpdatedAndFinish()
        }

    }
}