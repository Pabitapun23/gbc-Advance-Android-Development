package com.jk.room_kotlin.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jk.room_kotlin.data.Note
import com.jk.room_kotlin.databinding.NoteItemBinding

class NoteAdapter(private val context: Context, var notesList: MutableList<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        return NoteHolder(NoteItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val currentNote: Note = notesList[position]
        holder.bind(currentNote)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class NoteHolder(b: NoteItemBinding) : RecyclerView.ViewHolder(b.getRoot()) {
        var binding: NoteItemBinding

        fun bind(currentNote: Note?) {

            if (currentNote != null) {
                binding.textViewTitle.setText(currentNote.title)
                binding.textViewDescription.setText(currentNote.description)
                binding.textViewPriority.setText(currentNote.priority.toString())
            }

        }

        init {
            binding = b
        }
    }
}
