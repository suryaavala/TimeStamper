package com.sardox.timestamper.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.utils.TimestampIcon;

import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<Category> {
    private List<Category> categories;
    private List<TimestampIcon> icons;

    public CategoryListAdapter(@NonNull Context context, List<Category> categories, List<TimestampIcon> icons) {
        super(context, 0, categories);
        this.categories = categories;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            item = LayoutInflater.from(getContext()).inflate(R.layout.category_item_with_icon, parent, false);
        }
        Category category = categories.get(position);
        ImageView imageView = item.findViewById(R.id.category_icon);
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), icons.get(category.getIcon_id()).getDrawable_id()));
        TextView textView = item.findViewById(R.id.category_name);
        textView.setText(category.getName());
        return item;
    }
}
