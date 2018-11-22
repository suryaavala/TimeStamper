package com.sardox.timestamper.dialogs

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.widget.AdapterView
import android.widget.ListView
import com.sardox.timestamper.R
import com.sardox.timestamper.objects.Category
import com.sardox.timestamper.utils.TimestampIcon
import kotlinx.android.synthetic.main.category_list.*


class CategoryListBottomSheet(context: Context,
                              dialogTitle: String,
                              allCategories: List<Category>,
                              icons: List<TimestampIcon>,
                              newCategoryId: (category: Category) -> Unit) : BottomSheetDialog(context) {
    init {

        setContentView(R.layout.category_list)
        updateTitle(dialogTitle)

        val adapter = CategoryListAdapter(context, allCategories, icons)
        val listView = findViewById<ListView>(R.id.category_listview)
        if (listView != null) {
            listView.adapter = adapter
            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                newCategoryId.invoke(allCategories[position])
                dismiss()
            }
            show()
        }
    }

    private fun updateTitle(text: String) {
        category_list_title.text = text
    }
}