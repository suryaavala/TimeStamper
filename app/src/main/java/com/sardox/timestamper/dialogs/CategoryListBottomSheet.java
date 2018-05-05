package com.sardox.timestamper.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.TimestampIcon;

import java.util.List;


public class CategoryListBottomSheet extends BottomSheetDialog {
    public CategoryListBottomSheet(@NonNull Context context,
                                   String dialogTitle,
                                   final List<Category> allCategories,
                                   final List<TimestampIcon> icons,
                                   final Consumer<Category> newCategoryId) {
        super(context);

        setContentView(R.layout.category_list);
        updateTitle(dialogTitle);

        CategoryListAdapter adapter = new CategoryListAdapter(context, allCategories, icons);
        ListView listView = findViewById(R.id.category_listview);
        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    newCategoryId.accept(allCategories.get(position));
                    dismiss();
                }
            });
            show();
        }
    }

    private void updateTitle(String text) {
        TextView title = findViewById(R.id.category_list_title);
        if (title != null) {
            title.setText(text);
        }
    }
}
