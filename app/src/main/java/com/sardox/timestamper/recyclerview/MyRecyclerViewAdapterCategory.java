package com.sardox.timestamper.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.R;
import com.sardox.timestamper.utils.Consumer;


import java.util.List;


public class MyRecyclerViewAdapterCategory extends RecyclerView.Adapter<MyRecyclerViewAdapterCategory.MyViewHolderCategory> {

    private List<Category> categories;
    private Consumer<Category> selected_category;

    public MyRecyclerViewAdapterCategory(List<Category> categories, Consumer<Category> selected_category) {
        this.selected_category = selected_category;
        this.categories = categories;
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
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class MyViewHolderCategory extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mCategoryTextView;


        public MyViewHolderCategory(View itemView) {
            super(itemView);
            mCategoryTextView = (TextView) itemView.findViewById(R.id.CategoryTextView);
            mCategoryTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selected_category.accept(categories.get(getAdapterPosition()));
        }
    }
}