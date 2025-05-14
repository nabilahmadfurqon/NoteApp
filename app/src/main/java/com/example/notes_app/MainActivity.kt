package com.example.notes_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.notes_app.R

class MainActivity : AppCompatActivity(), NoteAdapter.OnNoteClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var fabAddNote: FloatingActionButton
    private var noteList = mutableListOf<Note>()
    private lateinit var dbHelper: NoteDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewNotes)
        fabAddNote = findViewById(R.id.fabAddNote)

        dbHelper = NoteDatabaseHelper(this)
        noteAdapter = NoteAdapter(noteList, this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = noteAdapter

        fabAddNote.setOnClickListener {
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("isNewNote", true)
            startActivity(intent)
        }

        loadNotesFromDatabase()
    }

    private fun loadNotesFromDatabase() {
        noteList.clear()
        noteList.addAll(dbHelper.getAllNotes())
        noteAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        loadNotesFromDatabase()
    }

    override fun onNoteClick(position: Int) {
        val note = noteList[position]
        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra("isNewNote", false)
        intent.putExtra("noteId", note.noteId)
        intent.putExtra("title", note.title)
        intent.putExtra("content", note.mainNote)
        intent.putExtra("categoryId", note.categoryId)
        intent.putExtra("createdAt", note.createdAt)
        startActivity(intent)
    }
}