package com.example.todoapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDataBaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "notesapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        //query to create table
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT)"
        //excute the query
        db?.execSQL(createTableQuery)
    }

    //we avoid to have multiple tables with same name and create a new table
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    //Add
    fun insertNote(note: Note){
        val db = writableDatabase
        val values = ContentValues().apply { this
            put(COLUMN_TITLE,note.title)
            put(COLUMN_CONTENT,note.content)
        }
        db.insert(TABLE_NAME,null,values)
        db.close()
    }

    //Retrieve data
    //this function needs to return a List(:List<Note>)
    fun getAllNotes(): List<Note>{
        val noteList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        //cursor is used to iterate through the rows of the table
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

            val note = Note(id,title,content)
            noteList.add(note)
        }
        cursor.close()
        db.close()
        //return is necessary bcz we need to display these retrieve data in mainActivity
        return noteList
    }

    //updateNote
    //pass note as the parameter
    fun updateNote(note:Note){
        val db = writableDatabase
        val values = ContentValues().apply { this
            put(COLUMN_TITLE,note.title)
            put(COLUMN_CONTENT, note.content)
        }
        //identify which data to be update using column id
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME,values,whereClause,whereArgs)
        db.close()
    }

    //getOneNote by its id
    //this function needs to return a Note(:Note)
    fun getNoteById(noteId:Int): Note{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteId"
        val cursor = db.rawQuery(query,null)
        //move the cursor to the first row
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

        cursor.close()
        db.close()
        return Note(id,title,content)
    }

    //1)delete function
    //2)set it on delete Btn(for that go to notesAdapter)
    fun deleteNote(noteId: Int){
        //deleting means modifying the database so we wrote 'writableDatabase'
        val db = writableDatabase
        //for the question mark it will take whereArgs(? = whereArgs)
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME,whereClause,whereArgs)
        db.close()
    }

}