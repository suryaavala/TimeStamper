package com.sardox.timestamper.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.sardox.timestamper.R;
import com.sardox.timestamper.utils.Consumer;

public class SettingsDialog {

    public SettingsDialog(Context context, final Consumer<Boolean> onSettingsClosed) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.setting_dialog, null, false);
        builder.setView(view)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onSettingsClosed.accept(true);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onSettingsClosed.accept(true);
                    }
                })
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSettingsClosed.accept(true);
                    }
                });
        AlertDialog dialog = builder.create();
        builder.show();
    }
}
