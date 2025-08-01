package com.lyra.notation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.lyra.notation.extension.CustomTypefaceSpan
import com.lyra.notation.extension.getFiraCodeFont
import com.lyra.notation.extension.getOpenSansFont
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.spans.CodeSpan
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import org.commonmark.node.Code
import org.commonmark.node.FencedCodeBlock

class PreviewActivity: BaseActivity(R.layout.activity_preview_note) {

    private lateinit var noteBody: TextView
    private lateinit var editButton: Button
    private lateinit var markwon: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun onResume() {
        super.onResume()

        val noteItemId = intent.getStringExtra("NOTE_ITEM_ID") ?: return showMessageNoteNotFoundAndFinish()
        val updatedNode = NoteStorage.getNoteByNoteItemId(noteItemId) ?: return showMessageNoteNotFoundAndFinish()

        val noteTitleTextView = findViewById<TextView>(R.id.previewNoteTitle)
        val noteDescriptionTextView = findViewById<TextView>(R.id.previewNoteDescription)

        val noteItem = NoteStorage.getNoteItems().find { noteItem -> noteItem.id == noteItemId } ?: return showMessageNoteNotFoundAndFinish()
        noteTitleTextView.text = noteItem.title
        noteDescriptionTextView.text = noteItem.description

        markwon.setMarkdown(noteBody, updatedNode.body)
    }

    private fun showMessageNoteNotFoundAndFinish() {
        Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun init() {
        val noteItemId = intent.getStringExtra("NOTE_ITEM_ID") ?: return finish()
        val note = NoteStorage.getNoteByNoteItemId(noteItemId) ?: return showMessageNoteNotFoundAndFinish()

        val noteTitleTextView = findViewById<TextView>(R.id.previewNoteTitle)
        val noteDescriptionTextView = findViewById<TextView>(R.id.previewNoteDescription)

        val noteItem = NoteStorage.getNoteItems().find { noteItem -> noteItem.id == noteItemId } ?: return showMessageNoteNotFoundAndFinish()
        noteTitleTextView.text = noteItem.title
        noteDescriptionTextView.text = noteItem.description

        noteBody = findViewById(R.id.previewNoteBody)
        noteBody.typeface = getOpenSansFont(this)
        editButton = findViewById(R.id.editNoteButton)


        val that = this
        markwon = Markwon.builder(this)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(this))
            .usePlugin(TablePlugin.create(this))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                    builder.setFactory(Code::class.java) { _, _ ->
                        CustomTypefaceSpan(getFiraCodeFont(that))
                    }

                    builder.setFactory(FencedCodeBlock::class.java) { _, _ ->
                        CustomTypefaceSpan(getFiraCodeFont(that))
                    }
                }
            })
            .build()

        markwon.setMarkdown(noteBody, note.body)



//        noteBody.setOnClickListener { event ->
//            val layout = noteBody.layout ?: return@setOnClickListener
//            val x = event.x
//            val y = event.y.toInt()
//
//            val offset = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x)
//
//            val lines = note.body.lines().toMutableList()
//            var updated = false
//
//            var charCount = 0
//            for (i in lines.indices) {
//                val line = lines[i]
//                val start = charCount
//                val end = charCount + line.length
//
//                if (offset in start..end) {
//                    if (line.contains("- [ ]")) {
//                        lines[i] = line.replaceFirst("- [ ]", "- [x]")
//                        updated = true
//                    } else if (line.contains("- [x]")) {
//                        lines[i] = line.replaceFirst("- [x]", "- [ ]")
//                        updated = true
//                    }
//                    break
//                }
//                charCount = end + 1
//            }
//
//            if (updated) {
//                val newBody = lines.joinToString("\n")
//                NoteStorage.updateNotesFile(
//                    NoteStorage.getNotes().map {
//                        if (it.noteItemId == noteItemId) it.copy(body = newBody) else it
//                    }
//                )
//                markwon.setMarkdown(noteBody, newBody)
//            }
//        }

        editButton.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            intent.putExtra("NOTE_ITEM_ID", noteItemId)
            startActivity(intent)
        }

    }
}
