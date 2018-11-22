package com.sardox.timestamper.dialogs

import android.content.Context
import android.support.v7.app.AlertDialog

import com.sardox.timestamper.R
import com.sardox.timestamper.utils.Consumer

class ConfirmRemoveCategoryDialog(context: Context, confirmed: Consumer<Boolean>) {

    init {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(R.string.delete)
        dialog.setMessage(R.string.are_you_sure_delete)
        dialog.setPositiveButton(R.string.yes) { _, _ -> confirmed.accept(true) }
        dialog.setNegativeButton(R.string.no) { dialog2, _ -> dialog2.dismiss() }
        dialog.show()
    }
}