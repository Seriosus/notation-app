package com.lyra.notation

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lyra.notation.extension.findAndKill
import com.lyra.notation.model.Note
import com.lyra.notation.model.NoteItem
import com.lyra.notation.model.PartialNote
import com.lyra.notation.model.PartialNoteItem
import java.io.File
import java.util.UUID

private const val NOTE_ITEMS_FILE = "note_items.json"
private const val NOTES_FILE = "notes.json"

object NoteStorage {

    private lateinit var appContext: Context
    private val gson = Gson()

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun initNoteFiles() {
        val filesDir = appContext.filesDir
        val noteItemsFile = File(filesDir, NOTE_ITEMS_FILE)
        val notesFile = File(filesDir, NOTES_FILE)

        if (!noteItemsFile.exists()) noteItemsFile.writeText("[]")
        if (!notesFile.exists()) notesFile.writeText("[]")

        val itemsJSON = noteItemsFile.readText()
        val notesJSON = notesFile.readText()

        if (itemsJSON == "[]" && notesJSON != "[]") {
            notesFile.writeText("[]")
        }
    }

    fun getNoteItems(): List<NoteItem> {
        val noteItemsFile = File(appContext.filesDir, NOTE_ITEMS_FILE)
        val itemsJSON = noteItemsFile.readText()
        val type = object: TypeToken<List<NoteItem>>() {}.type
        val noteItems = gson.fromJson(itemsJSON, type) ?: emptyList<NoteItem>()
        return noteItems.sortedBy { noteItem -> noteItem.orderId }
    }

    fun getNotes(): List<Note> {
        val notesFile = File(appContext.filesDir, NOTES_FILE)
        val notesJSON = notesFile.readText()
        val type = object: TypeToken<List<Note>>() {}.type
        return gson.fromJson(notesJSON, type)
    }

    fun updateFiles(noteItems: List<NoteItem>, notes: List<Note>): Boolean {
        updateNoteItemsFile(noteItems)
        updateNotesFile(notes)
        return true
    }

    fun updateNotesFile(notes: List<Note>): Boolean {
        val filesDir = appContext.filesDir
        val notesFile = File(filesDir, NOTES_FILE)

        val updatedNotesJSON = gson.toJson(notes)

        notesFile.writeText(updatedNotesJSON)
        return true
    }

    fun updateNoteItemsFile(noteItems: List<NoteItem>): Boolean {
        val filesDir = appContext.filesDir
        val noteItemsFile = File(filesDir, NOTE_ITEMS_FILE)

        val updatedNoteItemsJSON = gson.toJson(noteItems)

        noteItemsFile.writeText(updatedNoteItemsJSON)
        return true
    }

    fun getNoteByNoteItemId(noteItemId: String): Note? = getNotes().find { note -> note.noteItemId == noteItemId }
    fun editWholeNoteByNoteItemId(noteItemId: String, partialNoteItem: PartialNoteItem, partialNote: PartialNote): Boolean {
        val noteItems = getNoteItems().toMutableList()
        val notes = getNotes().toMutableList()

        val noteItem = noteItems.find { noteItem -> noteItem.id == noteItemId } ?: return false
        val note = notes.find { note -> note.noteItemId == noteItemId } ?: return false

        notes.findAndKill { note -> note.noteItemId == noteItemId }
        noteItems.findAndKill { noteItem -> noteItem.id == noteItemId }

        val newNote = Note(
            noteItemId = noteItemId,
            body = partialNote.body ?: note.body,
            createdAt = note.createdAt,
            lastUpdatedAt = System.currentTimeMillis()
        )
        val newNoteItem = NoteItem(
            id = noteItemId,
            title = partialNoteItem.title ?: noteItem.title,
            description = partialNoteItem.description ?: noteItem.description,
            backgroundColor = partialNoteItem.backgroundColor ?: noteItem.backgroundColor,
            orderId = partialNoteItem.orderId ?: noteItem.orderId,
        )

        notes.add(newNote)
        noteItems.add(newNoteItem)

        updateFiles(noteItems, notes)
        return true
    }

    fun removeWholeNoteByNoteItemId(noteItemId: String): Boolean {
        val noteItems = getNoteItems().toMutableList()
        val notes = getNotes().toMutableList()

        notes.findAndKill { note -> note.noteItemId == noteItemId }
        noteItems.findAndKill { noteItem -> noteItem.id == noteItemId }

        updateFiles(noteItems, notes)
        return true
    }

    fun getLastOrderId(fromNoteItems: List<NoteItem>? = null): Int? {
        val notes = fromNoteItems ?: getNoteItems()
        return notes.maxOfOrNull { note -> note.orderId }
    }

    fun saveNote(title: String, description: String, bg: Int, body: String): NoteItem {
        val id = UUID.randomUUID().toString()
        val noteItems = getNoteItems().toMutableList()
        val notes = getNotes().toMutableList()
        val orderId = (getLastOrderId(noteItems) ?: -1) + 1

        val noteItem = NoteItem(
            id = id,
            title = title,
            description = description,
            backgroundColor = bg,
            orderId = orderId
        )
        
        val note = Note(
            noteItemId = id,
            body = body,
            createdAt = System.currentTimeMillis(),
            lastUpdatedAt = System.currentTimeMillis(),
        )

        noteItems.add(noteItem)
        notes.add(note)
        updateFiles(noteItems, notes)

        Log.d("NoteStorage", "✅ Nota guardada con ID $id y orderId $orderId")
        return noteItem
    }
}