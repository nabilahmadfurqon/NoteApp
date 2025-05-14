package com.example.notes_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "notes.db", null, 2) { // Versi database 2

    override fun onCreate(db: SQLiteDatabase) {
        // Buat tabel categories terlebih dahulu
        db.execSQL("""
            CREATE TABLE categories (
                category_id INTEGER PRIMARY KEY AUTOINCREMENT,
                category_name TEXT NOT NULL UNIQUE
            )
        """.trimIndent())

        // Insert beberapa kategori default
        db.execSQL("INSERT INTO categories (category_name) VALUES ('Umum')")
        db.execSQL("INSERT INTO categories (category_name) VALUES ('Pekerjaan')")
        db.execSQL("INSERT INTO categories (category_name) VALUES ('Pribadi')")
        db.execSQL("INSERT INTO categories (category_name) VALUES ('Belanja')")

        // Buat tabel notes dengan foreign key ke categories
        db.execSQL("""
            CREATE TABLE notes (
                note_id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                main_note TEXT NOT NULL,
                category_id INTEGER,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                FOREIGN KEY (category_id) REFERENCES categories(category_id)
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS notes")
            db.execSQL("DROP TABLE IF EXISTS categories")
            onCreate(db)
        }
    }

    // CRUD untuk Notes
    fun insertNote(note: Note): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", note.title)
            put("main_note", note.mainNote)
            put("category_id", note.categoryId)
            put("created_at", note.createdAt)
            put("updated_at", note.updatedAt)
        }
        return db.insert("notes", null, values)
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT n.note_id, n.title, n.main_note, n.category_id, 
                   n.created_at, n.updated_at, c.category_name
            FROM notes n
            LEFT JOIN categories c ON n.category_id = c.category_id
            ORDER BY n.updated_at DESC
        """.trimIndent(), null)

        while (cursor.moveToNext()) {
            notes.add(
                Note(
                    noteId = cursor.getInt(cursor.getColumnIndexOrThrow("note_id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    mainNote = cursor.getString(cursor.getColumnIndexOrThrow("main_note")),
                    categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                    categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                    updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("updated_at"))
                )
            )
        }
        cursor.close()
        return notes
    }

    fun updateNote(note: Note): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", note.title)
            put("main_note", note.mainNote)
            put("category_id", note.categoryId)
            put("updated_at", note.updatedAt)
        }
        return db.update("notes", values, "note_id=?", arrayOf(note.noteId.toString()))
    }

    fun deleteNote(id: Int): Int {
        val db = writableDatabase
        return db.delete("notes", "note_id=?", arrayOf(id.toString()))
    }

    // CRUD untuk Categories
    fun getAllCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM categories ORDER BY category_name", null)

        while (cursor.moveToNext()) {
            categories.add(
                Category(
                    categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                    categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"))
                )
            )
        }
        cursor.close()
        return categories
    }

    fun insertCategory(categoryName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("category_name", categoryName)
        }
        return db.insert("categories", null, values)
    }

    fun getCurrentDateTime(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }
}
