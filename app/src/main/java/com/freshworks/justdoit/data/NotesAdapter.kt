package com.freshworks.justdoit.data

import android.app.AlertDialog
import android.content.Context
import android.media.Image
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freshworks.justdoit.R
import com.freshworks.justdoit.model.KEY_CREATED_AT
import com.freshworks.justdoit.model.Note
import kotlinx.android.synthetic.main.activity_list.view.*
import kotlinx.android.synthetic.main.listrow.view.*
import kotlinx.android.synthetic.main.popup.view.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotesAdapter(
    private val list: ArrayList<Note>,
    private val context: Context
) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.listrow, parent, false)
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
        var image = itemView.iv_logo as ImageView
        var deletebtn = itemView.btn_delete
        var edit = itemView.btn_update

        fun bindViews(note: Note) {

            title.text = note.title
            description.text = note.description

            val dateFormat: java.text.DateFormat = DateFormat.getDateInstance()
            val formattedDate = dateFormat.format(Date(note.created_at ?: 1L).time)
            date.text = formattedDate.toString()
            //title.text = note.title

            deletebtn.setOnClickListener(this)

            edit.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            var position: Int = adapterPosition
            var note: Note = mlist[position]
            when (v!!.id) {
                deletebtn!!.id -> {
                    deleteChore(note.id!!)
                    mlist.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }

                edit!!.id -> {
                    editNote(note)
                }
            }
        }

        fun deleteChore(id: Int) {
            var db: NotesDataBaseHandler = NotesDataBaseHandler(mContext)
            db.deletnote(id)
        }

        fun editNote(note: Note) {

            var dialogBuilder: AlertDialog.Builder?
            var dialog: AlertDialog?
            var dbHandler: NotesDataBaseHandler = NotesDataBaseHandler(mContext)

            var view = LayoutInflater.from(context).inflate(R.layout.popup, null)
            var title = view.et_title
            var description = view.et_description
            var savebtn = view.btn_save
            var discard = view.btn_discard


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


                    Log.d("DBHANDLER","Succes update , ${note.id.toString()} ")


                    dbHandler.update(note)

                    notifyItemChanged(adapterPosition, note)


                    dialog!!.dismiss()


                } else {

                }
            }


        }
    }
}






