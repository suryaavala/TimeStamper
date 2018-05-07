package com.sardox.timestamper.recyclerview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.utils.Consumer;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CATEGORY_ITEM = 0;
    private final int ADD_BUTTON_ITEM = 1;
    private final int categoryColorSelected;
    private final int categoryColorDeselected;

    private Category selectedCategory = Category.Default;
    private Consumer<Category> categoryChangedCallback;
    private Consumer<Void> categoryAddCallback;
    private List<Category> categories;

    public CategoryAdapter(List<Category> categories, Consumer<Category> categoryChangedCallback, Consumer<Void> categoryAddCallback, Context context) {
        this.categoryAddCallback = categoryAddCallback;
        this.categoryChangedCallback = categoryChangedCallback;
        this.categories = categories;

        categoryColorSelected = ContextCompat.getColor(context, R.color.category_selected);
        categoryColorDeselected = ContextCompat.getColor(context, R.color.colorPrimary);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case CATEGORY_ITEM: {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recyclerview_category, viewGroup,
                                false);
                return new CategoryViewHolder(view);
            }
            case ADD_BUTTON_ITEM: {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recyclerview_category_add_button, viewGroup,
                                false);
                return new AddCategoryButtonViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case CATEGORY_ITEM:
                Category category = categories.get(position);
                CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
                categoryViewHolder.mCategoryTextView.setText(category.getName());
                if (selectedCategory.getCategoryID().equals(category.getCategoryID())){
                    categoryViewHolder.category_underline.setBackgroundColor(categoryColorSelected);
                } else {
                    categoryViewHolder.category_underline.setBackgroundColor(categoryColorDeselected);
                }
                break;
            case ADD_BUTTON_ITEM:
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (categories.size() == position) {
            return ADD_BUTTON_ITEM;
        } else {
            return CATEGORY_ITEM;
        }
    }

    public void setSelectedCategory(Category selected_category) {
        this.selectedCategory = selected_category;
    }

    @Override
    public int getItemCount() {
        return categories.size()+1;
    }


    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mCategoryTextView;
        private LinearLayout category_underline;

        CategoryViewHolder(View itemView) {
            super(itemView);
            mCategoryTextView = itemView.findViewById(R.id.CategoryTextView);
            category_underline = itemView.findViewById(R.id.recycler_category_underline);
            mCategoryTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectedCategory = categories.get(getAdapterPosition());
            categoryChangedCallback.accept(selectedCategory);
            notifyDataSetChanged();
        }
    }

    class AddCategoryButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mCategoryAdd;

        AddCategoryButtonViewHolder(View itemView) {
            super(itemView);
            mCategoryAdd = itemView.findViewById(R.id.category_item_button);
            mCategoryAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            categoryAddCallback.accept(null);
        }
    }
}