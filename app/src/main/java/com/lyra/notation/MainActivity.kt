package com.lyra.notation

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lyra.notation.adapter.NoteItemAdapter
import com.lyra.notation.adapter.NoteItemTouchHelperCallback
import com.lyra.notation.extension.adjustGridSpan
import com.lyra.notation.extension.isInLandscape
import com.lyra.notation.model.NoteItem

class MainActivity: BaseActivity(R.layout.activity_main) {

    private lateinit var noteItemAdapter: NoteItemAdapter
    private lateinit var notesRecycler: RecyclerView
    private var createDialogIsShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        notesInit()
    }

    override fun onResume() {
        super.onResume()
        refreshNoteItemsOnly()
    }

    private fun refreshNoteItemsOnly() = noteItemAdapter.updateAll(
        NoteStorage.getNoteItems(),
        notesRecycler
    )


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        notesRecycler.adjustGridSpan()
    }

    private fun notesInit() {
        val spanCount = if (this.isInLandscape()) 4 else 2

        val dropToDeleteZone = findViewById<ImageView>(R.id.deleteDropZone)
        notesRecycler = findViewById(R.id.notesRecycler)
        notesRecycler.layoutManager = GridLayoutManager(this, spanCount)

        val addWholeNoteButton = findViewById<Button>(R.id.fabCreateWholeNote)
        addWholeNoteButton.isClickable = true
        addWholeNoteButton.alpha = 1f

        val noteItems = NoteStorage.getNoteItems().toMutableList()

        noteItemAdapter = NoteItemAdapter(noteItems)
        notesRecycler.adapter = noteItemAdapter

        val callback = NoteItemTouchHelperCallback(noteItemAdapter, dropToDeleteZone, this)
        val touchHelper = ItemTouchHelper(callback)

        touchHelper.attachToRecyclerView(notesRecycler)
        noteItemAdapter.onNoteItemClick = { noteItem ->
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra("NOTE_ITEM_ID", noteItem.id)
            startActivity(intent)
        }

        fun addWholeNoteHandler() {
            if (createDialogIsShown) return
            createDialogIsShown = true
            addWholeNoteButton.isClickable = false
            addWholeNoteButton.alpha = 0.2f
            val inflater = LayoutInflater.from(this)
            val popupView = inflater.inflate(R.layout.dialog_create_note, null)

            val titleInput = popupView.findViewById<EditText>(R.id.noteTitle)
            val descriptionInput = popupView.findViewById<EditText>(R.id.noteDescription)

            val createButton = popupView.findViewById<Button>(R.id.createNoteButton)
            val closeButton = popupView.findViewById<TextView>(R.id.createNoteClose)

            val rootLayout = findViewById<FrameLayout>(R.id.rootLayout)
            rootLayout.addView(popupView)

            createButton.setOnClickListener {
                val title = titleInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()

                if (title.isEmpty()) {
                    titleInput.error = "Title is required ❌"
                    return@setOnClickListener
                }

                val noteItemId = noteItemAdapter.addWholeNote(title, description, Color.GREEN, "")
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(titleInput.windowToken, 0)
                titleInput.setText("")
                descriptionInput.setText("")
                rootLayout.removeView(popupView)
                createDialogIsShown = false
                addWholeNoteButton.isClickable = true
                addWholeNoteButton.alpha = 1f

                val intent = Intent(this, EditorActivity::class.java)
                intent.putExtra("NOTE_ITEM_ID", noteItemId)
                startActivity(intent)
            }

            closeButton.setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(titleInput.windowToken, 0)
                titleInput.setText("")
                descriptionInput.setText("")
                rootLayout.removeView(popupView)
                createDialogIsShown = false
                addWholeNoteButton.isClickable = true
                addWholeNoteButton.alpha = 1f
            }
        }

        addWholeNoteButton.setOnClickListener { addWholeNoteHandler() }
    }
}