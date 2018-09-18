package com.sardox.timestamper.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.sardox.timestamper.R
import com.sardox.timestamper.objects.Timestamp
import com.sardox.timestamper.utils.AppSettings
import java.util.*

class EditNoteDialog(context: Context, timestamp: Timestamp, appSettings: AppSettings, onNoteEdited: (note: String) -> Unit) {

    init {

        val builder = AlertDialog.Builder(context)
        val notesOnly = ArrayList<String>()
        @SuppressLint("InflateParams") val view = LayoutInflater.from(context).inflate(R.layout.text_input_note, null, false)
        val listView = view.findViewById<ListView>(R.id.quick_notes_listview)
        val quickNotesView = view.findViewById<LinearLayout>(R.id.quick_notes_view)
        val input = view.findViewById<EditText>(R.id.input)
        val timestampNote = timestamp.note
        input.setText(timestampNote)
        input.setSelection(timestampNote.length)
        val defaultString = context.getString(R.string.add_from_widget)
        if (timestampNote.equals(defaultString, ignoreCase = true)) {
            input.selectAll()
        }
        builder.setView(view)

        if (appSettings.shouldUseQuickNotes()) {
            quickNotesView.visibility = View.VISIBLE
            for (q in appSettings.quickNotes) {
                notesOnly.add(q.note)
            }
            val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, notesOnly)
            listView.adapter = adapter
        } else {
            quickNotesView.visibility = View.GONE
        }

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
            onNoteEdited.invoke(input.text.toString())
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }

        val alertDialog = builder.create()
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onNoteEdited.invoke(input.text.toString())
                alertDialog.dismiss()
            }
            false
        }

        alertDialog.show()

        if (appSettings.shouldShowKeyboardInAddNote()) {
            val window = alertDialog.window
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            alertDialog.dismiss()
            onNoteEdited.invoke(notesOnly[position])
        }
    }
}