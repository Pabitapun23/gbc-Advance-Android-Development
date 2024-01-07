package com.jk.room_kotlin.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

//Note Data Access Object

@Dao
interface NoteDAO {

    // INSERT INTO table_notes VALUES("check query", "is it working", 5)

    @Insert
    fun insertNote(newNote: Note)


//    @Insert(onConflict = OnConflictStrategy.REPLACE) - REPLACE = Replace the existing data but we can loose our data
    // - ABORT = terminate
    // suspend - keyword that represents the function is working in the background
    // OnConflictStrategy.ABORT (default) : to roll back the transaction on conflict.
    // OnConflictStrategy.REPLACE : replace the existing rows with the new rows.
    // OnConflictStrategy.IGNORE : keep the existing rows.


    @Update
    fun updateNote(note: Note)


    @Delete
    fun deleteNote(note: Note)


    //  @Query("DELETE FROM table_notes") - have to specify what we want to do on all the elements
    @Query("DELETE FROM table_notes")
    fun deleteAllNotes()


    @Query("SELECT * FROM table_notes ORDER BY title")
    fun getAllNotes() : LiveData<List<Note>>
}