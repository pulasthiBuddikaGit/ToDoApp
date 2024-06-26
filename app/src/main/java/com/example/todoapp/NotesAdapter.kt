package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

//this adapter is responsible for provide the views that represent in a data set
class NotesAdapter(private var notes: List<Note>, context: Context): RecyclerView.Adapter<NotesAdapter.NoteViewHolder>(){

    //access notesDatabaseHelper
    private val db:NotesDataBaseHelper = NotesDataBaseHelper(context)
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //initialize views and buttons
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        //if error says item is null , It can be happened for 2 situations
        //1.either its not initialize
        //2.wrong id has assigned
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        //initialize delete btn
        val deleteBtn : ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item , parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        //user entered values will be display in these views
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context, UpdateNoteActivity::class.java).apply {
                //pass the id to updateNote activity for update relevant item
                putExtra("note_id",note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        //2)call the deleteNote method
        holder.deleteBtn.setOnClickListener{
            db.deleteNote(note.id)
            //we need to refresh data whenever we delete a note
            //require array as a argument
            refreshData(db.getAllNotes())
            //toast msg for adapter
            Toast.makeText(holder.itemView.context,"Note deleted",Toast.LENGTH_SHORT).show()
        }
    }

    //refresh the notes when new note added
    fun refreshData(newNotes : List<Note>){
        notes = newNotes
        notifyDataSetChanged()
    }
}