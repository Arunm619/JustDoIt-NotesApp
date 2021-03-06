package com.freshworks.justdoit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freshworks.justdoit.R
import com.freshworks.justdoit.data.NotesAdapter
import com.freshworks.justdoit.data.NotesDataBaseHandler
import com.freshworks.justdoit.model.Note
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup.*
import kotlinx.android.synthetic.main.popup.view.*
import org.w3c.dom.Node

class ListActivity : AppCompatActivity() {


    private var notesList: ArrayList<Note>? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    var dbHandler: NotesDataBaseHandler? = null
    var adapter: NotesAdapter? = null
    var lists: ArrayList<Note>? = null

    private var dialogBuilder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        notesList = ArrayList()
        linearLayoutManager = LinearLayoutManager(this)
        dbHandler = NotesDataBaseHandler(this)
        adapter = NotesAdapter(notesList!!, this, linearLayoutManager!!)


        //setting up rv

        rv_list.adapter = adapter
        rv_list.layoutManager = linearLayoutManager


        populateData()

        fab_Add.setOnClickListener {
            createPopUpMenu()
        }


        //val rv_list = findViewById(R.id.rv_list) as RecyclerView

        rv_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && fab_Add.isShown)
                    fab_Add.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab_Add.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

    }

    fun populateData() {
        lists = dbHandler!!.readNotes()


        lists?.reverse()

        notesList?.clear()

        for (i in lists!!.iterator())
            notesList?.add(i)


        adapter!!.notifyDataSetChanged()

    }

    fun createPopUpMenu() {

        var view = layoutInflater.inflate(R.layout.popup, null)
        var title = view.et_title
        var description = view.et_description

        var save_btn = view.btn_save
        var discard_btn = view.btn_discard

        dialogBuilder = AlertDialog.Builder(this).setView(view)
        alertDialog = dialogBuilder!!.create()
        alertDialog!!.show()

        discard_btn.setOnClickListener {
            alertDialog!!.dismiss()
        }

        save_btn.setOnClickListener {


            if (TextUtils.isEmpty(title.text.toString())) {
                val snack = Snackbar.make(view, "Please Enter Title ", Snackbar.LENGTH_SHORT)
                snack.show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(description.text.toString())) {
                val snack = Snackbar.make(view, "Please Enter Description ", Snackbar.LENGTH_SHORT)
                snack.show()
                return@setOnClickListener
            }


            val note = Note()
            note.title = title.text.toString()
            note.description = description.text.toString()


            dbHandler!!.createNote(note)


            populateData()
            alertDialog!!.dismiss()


        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // return super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

//        if (item!!.itemId == R.id.create_new) {
//            //startActivity(Intent(this,MainActivity::class.java))
//            createPopUpMenu()
//        } else
        if (item!!.itemId == R.id.about) {
            //go to about page..

            Toast.makeText(this, "To be added! and generating fake data", Toast.LENGTH_SHORT).show()

            for (i in (1..100)) {
                val note = Note()
                note.title = i.toString()
                note.description = "This article is about the number - $i"


                dbHandler!!.createNote(note)

            }

            adapter?.notifyDataSetChanged()
        }

        return super.onOptionsItemSelected(item)


    }

}
