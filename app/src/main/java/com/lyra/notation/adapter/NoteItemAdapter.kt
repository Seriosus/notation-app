package com.lyra.notation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.lyra.notation.NoteStorage
import com.lyra.notation.model.NoteItem
import com.lyra.notation.R
import kotlin.collections.addAll
import kotlin.text.clear

class NoteItemAdapter (
    private val noteItems: MutableList<NoteItem>
): RecyclerView.Adapter<NoteItemAdapter.NoteViewHolder>() {

    var onNoteItemClick: ((NoteItem) -> Unit)? = null
    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.noteItemContainer)
        val titleText: TextView = itemView.findViewById(R.id.noteItemTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)

        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = noteItems.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteItems[position]

        holder.titleText.text = note.title
        holder.card.setCardBackgroundColor(note.backgroundColor)

        holder.itemView.post {
            val width = holder.itemView.width

            if (width > 0) {
                holder.itemView.layoutParams.height = width
                holder.itemView.requestLayout()
            }
        }

        holder.itemView.setOnClickListener {
            onNoteItemClick?.invoke(note)
        }
    }

    fun moveItem(from: Int, to: Int) {
        if (from == to) return

        val moved = noteItems.removeAt(from)
        noteItems.add(to, moved)

        notifyItemMoved(from, to)

        noteItems.forEachIndexed { index, item ->
            noteItems[index] = item.copy(orderId = index)
        }

        NoteStorage.updateNoteItemsFile(noteItems)
    }

    fun getNoteItemByIndex(position: Int): NoteItem = noteItems[position]

    fun addWholeNote(title: String, description: String, bg: Int, body: String): String {
        val noteItem = NoteStorage.saveNote(title, description, Color.GREEN, "")

        noteItems.add(noteItem)
        notifyItemInserted(noteItems.lastIndex)
        return noteItem.id
    }

    fun deleteNoteAt(position: Int) {
        val deleted = noteItems.removeAt(position)

        NoteStorage.removeWholeNoteByNoteItemId(deleted.id)
        notifyItemRemoved(position)

        noteItems.forEachIndexed { index, item ->
            noteItems[index] = item.copy(orderId = index)
        }

        NoteStorage.updateNoteItemsFile(noteItems)
    }

    fun updateAll(newItems: List<NoteItem>, recyclerView: RecyclerView) {
        noteItems.clear()
        noteItems.addAll(newItems)
        notifyDataSetChanged()

        recyclerView.post {
            recyclerView.invalidateItemDecorations()
            recyclerView.requestLayout()
        }
    }
}