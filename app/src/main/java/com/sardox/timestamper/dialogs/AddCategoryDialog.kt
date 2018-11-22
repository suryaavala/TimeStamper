package com.sardox.timestamper.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.EditText

import com.sardox.timestamper.R
import com.sardox.timestamper.objects.Category
import com.sardox.timestamper.recyclerview.IconAdapter
import com.sardox.timestamper.types.JetUUID
import com.sardox.timestamper.utils.Consumer
import com.sardox.timestamper.utils.TimestampIcon

class AddCategoryDialog(context: Context, icons: List<TimestampIcon>, onCategoryCreated: (category: Category) -> Unit) {

    init {
        val builder = AlertDialog.Builder(context)
        @SuppressLint("InflateParams") val viewInflated = LayoutInflater.from(context).inflate(R.layout.new_category, null, false)
        val input = viewInflated.findViewById<EditText>(R.id.input_cat)
        val iconPicker = IconAdapter(icons, Consumer { icon -> input.setText(icon.description) }, context)

        val iconRecycler = viewInflated.findViewById<RecyclerView>(R.id.recyclerView_icon)
        iconRecycler.adapter = iconPicker
        iconRecycler.setHasFixedSize(true)

        val linearLayoutManagerCat = LinearLayoutManager(context)
        linearLayoutManagerCat.orientation = LinearLayoutManager.HORIZONTAL
        iconRecycler.layoutManager = linearLayoutManagerCat

        builder.setView(viewInflated)
        builder.setPositiveButton(R.string.save) { dialog, _ ->
            val lastAdapterPosition = iconPicker.lastSelected // which icon was selected
            val newCategory = Category(input.text.toString(), JetUUID.randomUUID(), lastAdapterPosition)
            onCategoryCreated.invoke(newCategory)
            iconPicker.destroy()
            dialog.cancel()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            iconPicker.destroy()
            dialog.cancel()
        }
        builder.show()
    }
}