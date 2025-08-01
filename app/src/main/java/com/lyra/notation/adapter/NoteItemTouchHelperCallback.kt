package com.lyra.notation.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lyra.notation.extension.getViewRect
import com.lyra.notation.extension.withToleranceShrink

class NoteItemTouchHelperCallback (
    private val adapter: NoteItemAdapter,
    private val dropToDeleteZone: View,
    private val context: Context
): ItemTouchHelper.Callback() {

    private var lastDraggedViewHolder: RecyclerView.ViewHolder? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        adapter.moveItem(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }

    override fun isLongPressDragEnabled(): Boolean = true

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                lastDraggedViewHolder = viewHolder
                dropToDeleteZone.visibility = View.VISIBLE

                dropToDeleteZone.post {
                    dropToDeleteZone.requestLayout()
                    dropToDeleteZone.invalidate()
                }
            }

            ItemTouchHelper.ACTION_STATE_IDLE -> {
                dropToDeleteZone.visibility = View.GONE
                lastDraggedViewHolder?.let {
                    val itemView = it.itemView

                    val noteItemRect = getViewRect(itemView).withToleranceShrink(70)
                    val dropToDeleteZoneRect = getViewRect(dropToDeleteZone)

                    if (Rect.intersects(noteItemRect, dropToDeleteZoneRect)) {
                        val position = it.adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val noteItem = adapter.getNoteItemByIndex(position)

                            AlertDialog.Builder(context)
                                .setTitle("Confirmation")
                                .setMessage("¿Are you sure to delete note \"${noteItem.title}\"?")
                                .setPositiveButton("Yes, I do") { _, _ ->
                                    adapter.deleteNoteAt(position)
                                }
                                .setNegativeButton("No, cancel", null)
                                .show()
                        }
                    }
                }

                lastDraggedViewHolder = null
            }
        }
    }
}


