// NoteAdapter.kt
package com.example.notes_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class NoteAdapter(
    private var fullList: List<Note>,
    private val listener: OnNoteClickListener
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var filteredList = fullList.toMutableList()

    interface OnNoteClickListener {
        fun onNoteClick(position: Int)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvNoteTitle)
        private val tvCategory: Chip = itemView.findViewById(R.id.tvNoteCategory)
        private val tvContent: TextView = itemView.findViewById(R.id.tvNoteContent)
        private val tvDate: TextView = itemView.findViewById(R.id.tvNoteDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onNoteClick(position)
                }
            }
        }

        fun bind(note: Note) {
            tvTitle.text = note.title
            tvCategory.text = note.categoryName ?: "Umum"
            tvContent.text = note.mainNote
            tvDate.text = "Diperbarui: ${note.updatedAt.substring(0, 10)}"

            val context = itemView.context
            when (note.categoryName) {
                "Pekerjaan" -> tvCategory.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.blue)
                "Pribadi" -> tvCategory.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.green)
                "Belanja" -> tvCategory.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.orange)
                else -> tvCategory.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.gray)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    fun setData(notes: List<Note>) {
        fullList = notes
        filteredList = notes.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            fullList.toMutableList()
        } else {
            fullList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        (it.categoryName?.contains(query, ignoreCase = true) ?: false)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): Note {
        return filteredList[position]
    }
}