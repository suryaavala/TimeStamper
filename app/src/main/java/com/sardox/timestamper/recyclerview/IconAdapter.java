package com.sardox.timestamper.recyclerview;

import android.content.Context;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;


import com.sardox.timestamper.R;

import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.TimestampIcon;

import java.util.List;


public class IconAdapter extends RecyclerView.Adapter<IconAdapter.MyViewHolderCategory> {

    private List<TimestampIcon> icons;
    private Consumer<TimestampIcon> consumer;

    private int lastSelected = 0;
    private Context context;

    public IconAdapter(List<TimestampIcon> icons, Consumer<TimestampIcon> consumer, Context context) {
        this.context = context;
        this.consumer = consumer;
        this.icons = icons;
    }

    @Override
    public IconAdapter.MyViewHolderCategory onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_icon_item, viewGroup,
                        false);
        return new MyViewHolderCategory(view);
    }

    @Override
    public void onBindViewHolder(IconAdapter.MyViewHolderCategory holder, int position) {

        if (lastSelected == position) holder.imageButton.setEnabled(false);
        else holder.imageButton.setEnabled(true);
        holder.imageButton.setImageDrawable(ContextCompat.getDrawable(context, icons.get(position).getDrawable_id()));
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public void destroy() {
        context = null;
    }

    class MyViewHolderCategory extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ImageButton imageButton;

        MyViewHolderCategory(View itemView) {
            super(itemView);

            imageButton = (ImageButton) itemView.findViewById(R.id.icon_item_button);
            imageButton.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (lastSelected != getAdapterPosition()) notifyItemChanged(lastSelected);
            lastSelected = getAdapterPosition();
            notifyItemChanged(lastSelected);

            consumer.accept(icons.get(getAdapterPosition()));
        }
    }

    public int getLastSelected() {
        return lastSelected;
    }
}
