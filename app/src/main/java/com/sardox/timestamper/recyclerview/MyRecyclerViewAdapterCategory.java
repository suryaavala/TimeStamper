package com.sardox.timestamper.recyclerview;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.MainActivity;
import com.sardox.timestamper.R;

import java.util.List;


public class MyRecyclerViewAdapterCategory extends RecyclerView.Adapter<MyRecyclerViewAdapterCategory.MyViewHolderCategory> {

    public List<Category> mCategoryList;
    MainActivity mainActivity;
    public int selectedPos = 0;

    public MyRecyclerViewAdapterCategory(List<Category> categoryList, MainActivity mainActivity) {
        this.mCategoryList = categoryList;
        this.mainActivity = mainActivity;
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
        Category category = mCategoryList.get(position);

        holder.mCategoryTextView.setText(category.getName());

        Log.e("aaa", "Category: onBindViewHolder for:" + category.getName() + ". id=" + category.getCategoryID());

        if (selectedPos == position) {
            // Here I am just highlighting the background
            if (mainActivity.useDark)  holder.itemView.setBackgroundResource(R.color.colorCatSeletedGrey);  else  holder.itemView.setBackgroundResource(R.color.colorCatSeleted);
            Log.e("aaa", "useDark= "+ String.valueOf(mainActivity.useDark));
            //holder.itemView.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
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


            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);


            mainActivity.LastCategoryFilter = mCategoryList.get(selectedPos).getCategoryID(); //!!!
            mainActivity.filterList();

            mainActivity.recyclerView.scrollToPosition(mainActivity.recyclerView.getAdapter().getItemCount() - 1);

        }
    }
}