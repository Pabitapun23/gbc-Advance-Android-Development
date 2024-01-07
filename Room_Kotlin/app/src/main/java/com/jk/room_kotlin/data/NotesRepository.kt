package com.jk.room_kotlin.data

import android.app.Application
import androidx.lifecycle.LiveData

class NotesRepository(application: Application) {

    // obtain the instance of the database and notesDAO
    private var db: AppDB? = null
    // to avoid null
    private var noteDAO = AppDB.getDB(application)?.noteDAO()
//    private val noteDAO = application?.let { AppDB.getDB(it)!!.noteDAO() }


    var allNotes: LiveData<List<Note>>? = noteDAO?.getAllNotes()

    init {
        this.db = AppDB.getDB(application)

    }

    fun insertNote(noteToInsert: Note) {
        AppDB.databaseQueryExecutor.execute{
            this.noteDAO?.insertNote(noteToInsert)
        }
    }

    fun updateNote(noteToUpdate: Note) {
        AppDB.databaseQueryExecutor.execute {
            this.noteDAO?.updateNote(noteToUpdate)
        }
    }

    fun deleteNote(noteToDelete: Note) {
        AppDB.databaseQueryExecutor.execute {
            this.noteDAO?.deleteNote(noteToDelete)
        }
    }

    fun deleteAllNotes() {
        AppDB.databaseQueryExecutor.execute {
            this.noteDAO?.deleteAllNotes()
        }
    }
}