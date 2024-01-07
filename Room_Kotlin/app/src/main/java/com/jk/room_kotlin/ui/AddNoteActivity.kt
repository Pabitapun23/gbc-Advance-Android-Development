package com.jk.room_kotlin.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jk.room_kotlin.R
import com.jk.room_kotlin.data.Note
import com.jk.room_kotlin.data.NotesRepository
import com.jk.room_kotlin.databinding.ActivityAddNoteBinding
import kotlinx.coroutines.launch

class AddNoteActivity : AppCompatActivity() {
    private var binding: ActivityAddNoteBinding? = null
    private val TAG = this.javaClass.canonicalName
    private lateinit var notesRepository: NotesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())

        binding!!.numberPickerPriority.minValue = 1
        binding!!.numberPickerPriority.maxValue = 10

        // initialize
        this.notesRepository = NotesRepository(application)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_note -> {
                saveNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = binding!!.editTextTitle.text.toString()
        val desc = binding!!.editTextDescription.text.toString()
        val priority = binding!!.numberPickerPriority.value

        val newNote = Note(title, desc, priority)

        Log.d(TAG, "saveNote: $newNote")

        // initiate the background task using lifecycle scope
        lifecycleScope.launch {
            notesRepository.insertNote(newNote)
        }

        // inform the user if the operation is successful
        // navigate the user
    }
}