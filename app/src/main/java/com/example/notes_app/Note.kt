package com.example.notes_app

data class Note(
    val noteId: Int,
    val title: String,
    val mainNote: String,
    val categoryId: Int,
    val categoryName: String?,
    val createdAt: String,
    val updatedAt: String
)