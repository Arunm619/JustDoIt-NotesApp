package com.freshworks.justdoit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.freshworks.justdoit.R
import com.freshworks.justdoit.data.NotesDataBaseHandler
import com.freshworks.justdoit.model.Note
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {


    var dbHandler: NotesDataBaseHandler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = NotesDataBaseHandler(this)

        checkDB()



        btn_save.setOnClickListener {

            if (validateFields()) {
                val note = Note()
                note.title = et_title.text.toString()
                note.description = et_description.text.toString()
                saveToDB(note)

            } else {
                return@setOnClickListener
            }

            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this,ListActivity::class.java))
            finish()

        }

        btn_discard.setOnClickListener {

            Toast.makeText(this, "Discarded", Toast.LENGTH_SHORT).show()

            finish()

        }

    }

    private fun checkDB() {

        if(dbHandler!!.getNotesCount()>0)
        {
            startActivity(Intent(this,ListActivity::class.java))
            finish()
        }


        val snack = Snackbar.make(rl_main, "Create your first note here!", Snackbar.LENGTH_INDEFINITE)
        snack.show()

    }


    fun validateFields(): Boolean {


        if (TextUtils.isEmpty(et_title.text.toString())) {
            val snack = Snackbar.make(rl_main, "Please Enter Title ", Snackbar.LENGTH_SHORT)
            snack.show()
            return false
        }

        if (TextUtils.isEmpty(et_description.text.toString())) {
            val snack = Snackbar.make(rl_main, "Please Enter Description ", Snackbar.LENGTH_SHORT)
            snack.show()
            return false
        }






        return true
    }

    fun saveToDB(note: Note) {
        dbHandler?.createNote(note)
    }
}
