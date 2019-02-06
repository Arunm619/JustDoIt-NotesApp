package com.freshworks.justdoit.data

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freshworks.justdoit.model.Note
import com.github.ivbaranov.mli.MaterialLetterIcon
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_list.view.*
import kotlinx.android.synthetic.main.listrow.view.*
import kotlinx.android.synthetic.main.popup.view.*
import java.security.PrivateKey
import java.text.DateFormat
import java.util.*


class NotesAdapter(
    private val list: ArrayList<Note>,
    private val context: Context,
    private val linearLayoutManager: LinearLayoutManager

) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    lateinit var llm: LinearLayoutManager

    init {
        llm = linearLayoutManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(com.freshworks.justdoit.R.layout.listrow, parent, false)
        return ViewHolder(view, context, list)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindViews(list[position])
    }


    inner class ViewHolder(itemView: View, context: Context, list: ArrayList<Note>) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var mContext = context
        var mlist = list
        var title = itemView.tv_title as TextView
        var description = itemView.tv_descripton as TextView
        var date = itemView.tv_date as TextView
        var image = itemView.iv_logo as MaterialLetterIcon
        var deletebtn = itemView.btn_delete
        var edit = itemView.btn_update

        fun bindViews(note: Note) {

            title.text = note.title
            description.text = note.description

            val dateFormat: java.text.DateFormat = DateFormat.getDateInstance()
            val formattedDate = dateFormat.format(Date(note.created_at ?: 1L).time)
            date.text = "created at : $formattedDate"
            //title.text = note.title

            image.letter = note.title!![0].toString()
            image.letterSize = 26
            val rnd = Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            image.shapeColor = color

            deletebtn.setOnClickListener(this)

            edit.setOnClickListener(this)


        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            val note: Note = mlist[position]
            when (v!!.id) {
                deletebtn!!.id -> {

                    notifyItemRemoved(position)
                    mlist.removeAt(position)

                    var Delete = true
                    var mySnack = Snackbar.make(itemView, "Deleted ${note.title}", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            mlist.add(position, note)

                            notifyItemInserted(position)

                            llm.scrollToPosition(position)

                            Delete = false


                        }

                    mySnack.show()


                    mySnack.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (Delete) {
                                //to be done after the user has let go the snack bar
                                deleteChore(note.id!!)
                                Toast.makeText(context, "Now only deletion happens", Toast.LENGTH_SHORT).show()

                            }
                        }

                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                        }
                    });


                }

                edit!!.id -> {
                    editNote(note)
                }
            }
        }

        fun deleteChore(id: Int) {
            val db: NotesDataBaseHandler = NotesDataBaseHandler(mContext)
            db.deletnote(id)
        }

        fun editNote(note: Note) {

            val dialogBuilder: AlertDialog.Builder?
            val dialog: AlertDialog?
            val dbHandler: NotesDataBaseHandler = NotesDataBaseHandler(mContext)

            val view = LayoutInflater.from(context).inflate(com.freshworks.justdoit.R.layout.popup, null)
            val title = view.et_title
            val description = view.et_description
            val savebtn = view.btn_save
            val discard = view.btn_discard


            title.setText(note.title)
            description.setText(note.description)


            dialogBuilder = AlertDialog.Builder(context).setView(view)
            dialog = dialogBuilder!!.create()
            dialog?.show()


            discard.setOnClickListener {

                dialog?.dismiss()
            }

            savebtn.setOnClickListener {
                var name = title.text.toString().trim()
                var desc = description.text.toString().trim()

                if (!TextUtils.isEmpty(name)
                    && !TextUtils.isEmpty(desc)
                ) {
                    //val note2 = Note()

                    note.title = name
                    note.description = desc


                    Log.d("DBHANDLER", "Succes update , ${note.id.toString()} ")


                    dbHandler.update(note)

                    notifyItemChanged(adapterPosition, note)



                    dialog!!.dismiss()


                } else {

                }
            }


        }
    }
}






