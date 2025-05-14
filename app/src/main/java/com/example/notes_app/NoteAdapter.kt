package com.example.notes_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notes_app.R

class NoteAdapter(
    private val noteList: List<Note>,
    private val listener: OnNoteClickListener
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    interface OnNoteClickListener {
        fun onNoteClick(position: Int)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvNoteTitle)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvNoteCategory)
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

            // Anda bisa menambahkan warna berbeda untuk kategori berbeda
            when (note.categoryName) {
                "Pekerjaan" -> tvCategory.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue))
                "Pribadi" -> tvCategory.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green))
                "Belanja" -> tvCategory.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orange))
                else -> tvCategory.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.gray))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(noteList[position])
    }

    override fun getItemCount(): Int = noteList.size
}