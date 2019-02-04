package com.freshworks.justdoit.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.contentValuesOf
import com.freshworks.justdoit.model.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotesDataBaseHandler(context : Context ) :
    SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery : String? =
                "CREATE TABLE $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY , " +
                        "$KEY_TITLE TEXT ," +
                        "$KEY_DESCRIPTION TEXT ," +
                        "$KEY_CREATED_AT LONG );"

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val deleteTable : String? = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(deleteTable)
        onCreate(db)
    }

    /*
    * CRUD - CREATE READ UPDATE DELETE
    * */

    fun createNote(note:Note)
    {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put(KEY_TITLE, note.title)
        values.put(KEY_DESCRIPTION, note.description)
        values.put(KEY_CREATED_AT, System.currentTimeMillis())

        db.insert(TABLE_NAME,null,values)
        Log.d("DBHANDLER","Succes inserting , ${note.title.toString()} ")
        db.close()
    }


    fun readNote(id : Int ):Note{
        val db: SQLiteDatabase = writableDatabase
       val cursor = db.query(TABLE_NAME,
           arrayOf(KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_CREATED_AT),
           "$KEY_ID ?= ",arrayOf(id.toString())
           ,null,null,null,null )

        if(cursor!=null)
        {
            cursor.moveToFirst()
        }

        val note = Note()

        note.id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
        note.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
        note.description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))

        val dateFormat : java.text.DateFormat = DateFormat.getDateInstance()
        var formattedDate = dateFormat.format(Date(cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))).time)

        note.created_at = cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))

        return note
    }

    fun update(note:Note):Int
    {

        val db : SQLiteDatabase = writableDatabase
        val values : ContentValues = ContentValues()
        values.put(KEY_TITLE, note.title)
        values.put(KEY_DESCRIPTION, note.description)
        values.put(KEY_CREATED_AT, System.currentTimeMillis())
        Log.d("DBHANDLER","Succes update , ${note.id.toString()} ")



        return db.update(TABLE_NAME, values, "$KEY_ID=?", arrayOf(note.id.toString()))


    }

    fun deletnote(id:Int){
        val db = writableDatabase
        db.delete(TABLE_NAME, KEY_ID+"=?", arrayOf(id.toString()))
        db.close()

    }

    fun getNotesCount():Int{
        val countQuery = "SELECT * FROM $TABLE_NAME "
        val db = readableDatabase
        val cursor = db.rawQuery(countQuery,null)
        return cursor.count

    }

    fun readNotes():ArrayList<Note>{
        val db : SQLiteDatabase = readableDatabase
        val list : ArrayList<Note> = ArrayList()
        val selectAll = "SELECT * FROM $TABLE_NAME"
        val cursor :Cursor = db.rawQuery(selectAll, null)

        if(cursor.moveToFirst())
        {
            do {

                val note = Note()

                note.id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                note.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                note.description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))
               // var dateFormat : java.text.DateFormat = DateFormat.getDateInstance()
                //var formattedDate = dateFormat.format(Date(cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))).time)
                note.created_at = cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))
                list.add(note)

            }while (cursor.moveToNext())
        }
    return list
    }


}
