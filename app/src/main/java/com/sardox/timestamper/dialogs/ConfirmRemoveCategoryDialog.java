package com.sardox.timestamper.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.sardox.timestamper.R;
import com.sardox.timestamper.utils.Consumer;

public class ConfirmRemoveCategoryDialog {

    public ConfirmRemoveCategoryDialog(Context context, final Consumer<Boolean> confirmed) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.delete);
        dialog.setMessage(R.string.are_you_sure_delete);
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmed.accept(true);
            }
        });
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
