package com.sardox.timestamper.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Timestamp;

import java.util.List;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private List<Timestamp> mStampsList;

    public MyRecyclerViewAdapter(List<Timestamp> stampsList) {
        this.mStampsList = stampsList;
    }

    @Override
    public MyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_item, viewGroup,
                        false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter.MyViewHolder holder, int position) {
        Timestamp timestamp = mStampsList.get(position);

        holder.recycler_timestamp_day.setText("11 Jan");
        holder.recycler_timestamp_category.setText("Sport");
        holder.recycler_timestamp_note.setText(timestamp.getNote());
        holder.recycler_timestamp_time.setText(timestamp.getTimestamp().toString());
        holder.recycler_timestamp_weekday.setText("Mon");

    }

    @Override
    public int getItemCount() {
        return mStampsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView recycler_timestamp_category, recycler_timestamp_weekday, recycler_timestamp_day, recycler_timestamp_note, recycler_timestamp_time;
       // private ImageButton

        public MyViewHolder(View itemView) {
            super(itemView);
            recycler_timestamp_category = (TextView) itemView.findViewById(R.id.recycler_timestamp_category);
            recycler_timestamp_day = (TextView) itemView.findViewById(R.id.recycler_timestamp_day);
            recycler_timestamp_note = (TextView) itemView.findViewById(R.id.recycler_timestamp_note);
            recycler_timestamp_time = (TextView) itemView.findViewById(R.id.recycler_timestamp_time);
         //   mGPSpinButton = (ImageButton) itemView.findViewById(R.id.r);
            recycler_timestamp_weekday = (TextView) itemView.findViewById(R.id.recycler_timestamp_weekday);

//            mNameTextView.setOnClickListener(this);
//            mNameTextView.setOnLongClickListener(this);
//            mCategoryTextView.setOnClickListener(this);
//            mGPSpinButton.setOnClickListener(this);
//            mSubtitleTextView.setOnClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {
//            if (v.getId() == mNameTextView.getId()) {
//
//                DatePickerFragment.time = mStampsList.get(getAdapterPosition()).getTime();
//                TimePickerFragment.pos = getAdapterPosition();
//                DatePickerFragment.fragmentmanager = fragmentmanager;
//                DialogFragment newFragment = new DatePickerFragment();
//                newFragment.show(fragmentmanager, "Change the date");
//            }
            return false;
        }

        @Override
        public void onClick(View v) {
//            int a = getAdapterPosition();
//            if (v.getId() == mNameTextView.getId() || v.getId() == mSubtitleTextView.getId()) {
//
//                showDialog(v, getAdapterPosition()); // input dialog -- for a note
//
//            } else if (v.getId() == mGPSpinButton.getId()) { //open google maps
//
//            } else if (v.getId() == mCategoryTextView.getId()) {  // spinner dialog -- change items category
//
//                showSpinner(v, getAdapterPosition());
//
//            } else if (v.getId() == mSubtitleTextView.getId()) {  // input dialog -- for a note - duplicate
//
//                showDialog(v, getAdapterPosition());
//
//            }
        }


        private void showSpinner(View v, final int pos) {

        } // spinner dialog -- change items category


        public void showDialog(View v, final int pos) {
        }  // input dialog -- for a note
    }
}
