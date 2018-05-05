package com.sardox.timestamper.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.QuickNote;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.utils.Consumer;

import java.util.ArrayList;
import java.util.List;

public class EditNoteDialog {

    public EditNoteDialog(final Context context, final Timestamp timestamp, final AppSettings appSettings, final Consumer<String> onNoteEdited) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final List<String> notesOnly = new ArrayList<>();
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.text_input_note, null, false);
        final ListView listView = view.findViewById(R.id.quick_notes_listview);
        final LinearLayout quickNotesView = view.findViewById(R.id.quick_notes_view);
        final EditText input = view.findViewById(R.id.input);
        final String timestampNote = timestamp.getNote();
        input.setText(timestampNote);
        input.setSelection(timestampNote.length());
        String defaultString = context.getString(R.string.add_from_widget);
        if (timestampNote.equalsIgnoreCase(defaultString)){
            input.selectAll();
        }
        builder.setView(view);

        if (appSettings.shouldUseQuickNotes()) {
            quickNotesView.setVisibility(View.VISIBLE);
            for (QuickNote q : appSettings.getQuickNotes()) {
                notesOnly.add(q.getNote());
            }
            final ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, notesOnly);
            listView.setAdapter(adapter);
        } else {
            quickNotesView.setVisibility(View.GONE);
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onNoteEdited.accept(input.getText().toString());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onNoteEdited.accept(input.getText().toString());
                    alertDialog.dismiss();
                }
                return false;
            }
        });

        alertDialog.show();

        if (appSettings.shouldShowKeyboardInAddNote()) {
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialog.dismiss();
                onNoteEdited.accept(notesOnly.get(position));
            }
        });
    }


}
