package com.sardox.timestamper.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.utils.Consumer;

public class EditNoteDialog {

    public EditNoteDialog(Context context, final Timestamp timestamp, final Consumer<String> onNoteEdited) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.text_input_note, null, false);
        final EditText input = (EditText) view.findViewById(R.id.input);
        input.setText(timestamp.getNote());
        builder.setView(view);
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
        builder.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


}
