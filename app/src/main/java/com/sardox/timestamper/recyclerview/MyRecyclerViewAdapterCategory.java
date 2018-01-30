package com.sardox.timestamper.recyclerview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.R;
import com.sardox.timestamper.utils.Consumer;


import java.util.List;


public class MyRecyclerViewAdapterCategory extends RecyclerView.Adapter<MyRecyclerViewAdapterCategory.MyViewHolderCategory> {

    private List<Category> categories;
    private Consumer<Category> category_changed_callback;
    //   private Context context;
    private final int category_color_selected;
    private final int category_color_deselected;

    public void setSelected_category(Category selected_category) {
        this.selected_category = selected_category;
    }

    private Category selected_category = Category.Default;


    public MyRecyclerViewAdapterCategory(List<Category> categories, Consumer<Category> category_changed_callback, Context context) {
        //   this.context = context;
        this.category_changed_callback = category_changed_callback;
        this.categories = categories;

        category_color_selected = ContextCompat.getColor(context, R.color.category_selected);
        category_color_deselected = ContextCompat.getColor(context, R.color.colorPrimary);

    }

    @Override
    public MyRecyclerViewAdapterCategory.MyViewHolderCategory onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_category, viewGroup,
                        false);
        return new MyViewHolderCategory(view);
    }

    @Override
    public void onBindViewHolder(MyRecyclerViewAdapterCategory.MyViewHolderCategory holder, int position) {
        Category category = categories.get(position);

        holder.mCategoryTextView.setText(category.getName());
        if (selected_category.getCategoryID().equals(category.getCategoryID()))
            holder.category_underline.setBackgroundColor(category_color_selected);
        else holder.category_underline.setBackgroundColor(category_color_deselected);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    class MyViewHolderCategory extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mCategoryTextView;
        private LinearLayout category_underline;

        MyViewHolderCategory(View itemView) {
            super(itemView);
            mCategoryTextView = (TextView) itemView.findViewById(R.id.CategoryTextView);
            category_underline = (LinearLayout) itemView.findViewById(R.id.recycler_category_underline);

            mCategoryTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selected_category = categories.get(getAdapterPosition());
            category_changed_callback.accept(selected_category);
            notifyDataSetChanged();
        }
    }
}