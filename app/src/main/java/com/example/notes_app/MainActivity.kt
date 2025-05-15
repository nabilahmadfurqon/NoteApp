// MainActivity.kt
package com.example.notes_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity(), NoteAdapter.OnNoteClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var searchView: SearchView
    private var noteList = mutableListOf<Note>()
    private lateinit var dbHelper: NoteDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewNotes)
        fabAddNote = findViewById(R.id.fabAddNote)
        searchView = findViewById(R.id.searchView)

        dbHelper = NoteDatabaseHelper(this)
        noteAdapter = NoteAdapter(noteList, this)

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = noteAdapter

        fabAddNote.setOnClickListener {
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("isNewNote", true)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                noteAdapter.filter(newText.orEmpty())
                return true
            }
        })

        loadNotesFromDatabase()
    }

    private fun loadNotesFromDatabase() {
        noteList = dbHelper.getAllNotes().toMutableList()
        noteAdapter.setData(noteList)
    }

    override fun onResume() {
        super.onResume()
        loadNotesFromDatabase()
    }

    override fun onNoteClick(position: Int) {
        val note = noteAdapter.getItemAt(position)
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