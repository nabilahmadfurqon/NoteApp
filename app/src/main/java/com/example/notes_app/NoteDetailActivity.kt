package com.example.notes_app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var toolbar: MaterialToolbar

    private var isNewNote = false
    private var noteId: Int = -1
    private lateinit var dbHelper: NoteDatabaseHelper
    private var categories = listOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        initializeViews()
        setupDatabase()
        setupToolbar()
        setupSpinner()
        loadNoteData()
        setupButtonListeners()
    }

    private fun initializeViews() {
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupDatabase() {
        dbHelper = NoteDatabaseHelper(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Perbaikan navigasi kembali
        }
    }

    private fun setupSpinner() {
        categories = dbHelper.getAllCategories()
        val categoryNames = categories.map { it.categoryName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun loadNoteData() {
        isNewNote = intent.getBooleanExtra("isNewNote", false)

        if (!isNewNote) {
            noteId = intent.getIntExtra("noteId", -1)
            etTitle.setText(intent.getStringExtra("title"))
            etContent.setText(intent.getStringExtra("content"))

            val categoryId = intent.getIntExtra("categoryId", 0)
            val categoryIndex = categories.indexOfFirst { it.categoryId == categoryId }
            if (categoryIndex >= 0) {
                spinnerCategory.setSelection(categoryIndex)
            }
        } else {
            btnDelete.visibility = Button.GONE
        }
    }

    private fun setupButtonListeners() {
        btnSave.setOnClickListener { saveNote() }
        btnDelete.setOnClickListener { deleteNote() }
    }

    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()
        val selectedCategory = categories[spinnerCategory.selectedItemPosition]

        if (title.isEmpty()) {
            etTitle.error = "Judul tidak boleh kosong"
            return
        }

        if (content.isEmpty()) {
            etContent.error = "Isi catatan tidak boleh kosong"
            return
        }

        val now = dbHelper.getCurrentDateTime()

        if (isNewNote) {
            val newNote = Note(
                0,
                title,
                content,
                selectedCategory.categoryId,
                selectedCategory.categoryName,
                now,
                now
            )
            dbHelper.insertNote(newNote)
            Toast.makeText(this, "Catatan baru disimpan", Toast.LENGTH_SHORT).show()
        } else {
            val updatedNote = Note(
                noteId,
                title,
                content,
                selectedCategory.categoryId,
                selectedCategory.categoryName,
                intent.getStringExtra("createdAt") ?: now,
                now
            )
            dbHelper.updateNote(updatedNote)
            Toast.makeText(this, "Catatan diperbarui", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun deleteNote() {
        dbHelper.deleteNote(noteId)
        Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show()
        finish()
    }
}