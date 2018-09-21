package com.sardox.timestamper.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.sardox.timestamper.R
import com.sardox.timestamper.objects.Timestamp
import com.sardox.timestamper.utils.AppSettings


class EditNoteDialog(context: Context, timestamp: Timestamp, appSettings: AppSettings, onNoteEdited: (note: String) -> Unit) {

    init {

        val builder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val view = LayoutInflater.from(context).inflate(R.layout.text_input_note, null, false)
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)

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

        if (appSettings.shouldUseQuickNotes) {
            quickNotesView.visibility = View.VISIBLE
            appSettings.quickNotes.mapNotNull { note -> note.note }.forEach {
                val chip = Chip(chipGroup.context).apply {
                    text = it
                    isClickable = true
                    isCheckable = true
                    setOnCheckedChangeListener { _, isChecked ->
                        when {
                            isChecked -> input.setText(it)
                            else -> if (input.text.toString() == it) input.setText("")
                        }
                    }
                }
                chipGroup.addView(chip)
            }
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
        with(input) {
            isFocusable = true
            requestFocus()
        }

        if (appSettings.shouldShowKeyboardInAddNote) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }
}