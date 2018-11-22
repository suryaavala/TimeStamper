package com.sardox.timestamper.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.sardox.timestamper.R

class SettingsDialog(context: Context, onSettingsClosed: () -> Unit) {

    init {
        val builder = AlertDialog.Builder(context, R.style.AppThemeLight)
        @SuppressLint("InflateParams") val view = LayoutInflater.from(context).inflate(R.layout.setting_dialog, null, false)
        builder.setView(view)
                .setOnCancelListener { onSettingsClosed.invoke() }
                .setOnDismissListener { onSettingsClosed.invoke() }
                .setPositiveButton("Back") { _, _ -> onSettingsClosed.invoke() }
        builder.create().show()
    }
}