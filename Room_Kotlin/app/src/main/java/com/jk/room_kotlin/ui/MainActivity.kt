package com.jk.room_kotlin.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jk.room_kotlin.R
import com.jk.room_kotlin.data.Note
import com.jk.room_kotlin.data.NotesRepository
import com.jk.room_kotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val TAG = this.javaClass.canonicalName
    private lateinit var notesList: MutableList<Note>
    private lateinit var noteAdapter: NoteAdapter
    // declare
    private lateinit var notesRepository: NotesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())

        // initialize
        this.notesRepository = NotesRepository(application)

        binding!!.btnAddNote.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            startActivity(intent)
        })

        notesList = mutableListOf()
        noteAdapter = NoteAdapter(applicationContext, notesList)
        binding!!.rvNotes.setAdapter(noteAdapter)
        binding!!.rvNotes.setLayoutManager(LinearLayoutManager(this))
        binding!!.rvNotes.setHasFixedSize(true)

        val simpleCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        //delete the current item from DB
                        Log.d(TAG,"onSwiped: trying to delete item : " + viewHolder.adapterPosition)
                        deleteNote(viewHolder.adapterPosition)
                    }
                }
            }

        val helper = ItemTouchHelper(simpleCallback)
        helper.attachToRecyclerView(binding!!.rvNotes)

    }

    override fun onStart() {
        super.onStart()

        // initiate the observer for live data for allNotes
        this.notesRepository.allNotes?.observe(this) { receivedNotes ->

            if (receivedNotes.isNotEmpty()) {
                Log.d(TAG, "onStart: ReceivedNotes : $receivedNotes")

                // option - 1
                // remove existing data
                notesList.clear()
                // add new data for the list associated with RecyclerView
//                notesList.addAll(receivedNotes)

               lifecycleScope.launch {
                   // option - 2
                   receivedNotes.forEach { note ->
                       if (notesList.contains(note)) {
                           Log.d(TAG, "onStart: object already present the list")
                       } else {
                           notesList.add(note)
                       }
                   }

                   noteAdapter.notifyDataSetChanged()
               }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_notes -> {
                // delete all the notes from database
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteNote(position: Int) {
        Log.d(TAG, "deleteNote: Trying to delete note at position $position")

        //ask for confirmation
        val confirmDialog = AlertDialog.Builder(this)
        confirmDialog.setTitle("Delete")
        confirmDialog.setMessage("Are you sure you want to delete this note?")
        confirmDialog.setNegativeButton("Cancel") { dialogInterface, i ->
            noteAdapter.notifyDataSetChanged()
            dialogInterface.dismiss()
        }
        confirmDialog.setPositiveButton("Yes") { dialogInterface, i ->
            //delete from database
        }
        confirmDialog.show()
    }
}