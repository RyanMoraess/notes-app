package com.ryanmoraess.notesapp.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ryanmoraess.notesapp.data.AppDatabase
import com.ryanmoraess.notesapp.data.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = AppDatabase.getInstance(application).noteDao()

    val notes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addNote(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            noteDao.insert(Note(title = title.trim(), content = content.trim()))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { noteDao.update(note) }
    }

    fun toggleDone(note: Note) {
        viewModelScope.launch { noteDao.update(note.copy(isDone = !note.isDone)) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { noteDao.delete(note) }
    }
}