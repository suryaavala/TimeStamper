package com.sardox.timestamper.recyclerview;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetTimestampFormat;
import com.sardox.timestamper.utils.AppSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private SortedList<Timestamp> sortedTimeStamps; // filteredTimestamps
    private final Comparator<Timestamp> mComparator;
    private List<Category> categories;
    private DisplayMetrics displayMetrics;
    private AppSettings appSettings;

    public MyRecyclerViewAdapter(final Comparator<Timestamp> mComparator, List<Category> categories, DisplayMetrics displayMetrics) {

        this.mComparator = mComparator;
        this.categories = categories;
        this.displayMetrics = displayMetrics;

        sortedTimeStamps = new SortedList<>(Timestamp.class, new SortedList.Callback<Timestamp>() {
            @Override
            public int compare(Timestamp o1, Timestamp o2) {
                return mComparator.compare(o1, o2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Timestamp oldItem, Timestamp newItem) {
                return false;
            }

            @Override
            public boolean areItemsTheSame(Timestamp item1, Timestamp item2) {
                return false;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
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
        int w = displayMetrics.widthPixels;
        holder.left_container.setMinimumWidth(w);
        Log.e("stamper", "setMinimumWidth: " + w);
        Log.e("stamper", "left_container w: " + holder.left_container.getWidth());

        Timestamp timestamp = sortedTimeStamps.get(position);

        JetTimestamp.now().toString(Locale.getDefault(), Calendar.getInstance().getTimeZone(), JetTimestampFormat.LongDateTime);

        Calendar calendar = Calendar.getInstance();
        TimeZone localTZ = calendar.getTimeZone();

        calendar.setTimeInMillis(timestamp.getTimestamp().toMilliseconds());
        String  format = "hh:mm:ss.SSS a";

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(localTZ);
        String hhmmss = sdf.format(calendar.getTime());

        SimpleDateFormat sdfDAY = new SimpleDateFormat("d MMM");
        sdfDAY.setTimeZone(localTZ);
        String dMMM = sdfDAY.format(calendar.getTime());

        SimpleDateFormat sdfWeekDay = new SimpleDateFormat("EEE");
        sdfWeekDay.setTimeZone(localTZ);
        String EEE = sdfWeekDay.format(calendar.getTime());

        holder.recycler_timestamp_day.setText(dMMM);
        holder.recycler_timestamp_category.setText(categories.get(timestamp.getCategoryId()).getName());
        holder.recycler_timestamp_note.setText(timestamp.getNote());
        holder.recycler_timestamp_time.setText(hhmmss);
        holder.recycler_timestamp_weekday.setText(EEE);
    }

    @Override
    public int getItemCount() {
        return sortedTimeStamps.size();
    }

    public void add(Timestamp timestamp) {
        sortedTimeStamps.add(timestamp);
    }

    public void remove(Timestamp timestamp) {
        sortedTimeStamps.remove(timestamp);
    }

    public void add(List<Timestamp> timestamps) {
        sortedTimeStamps.addAll(timestamps);
    }

    public void remove(List<Timestamp> timestamps) {
        sortedTimeStamps.beginBatchedUpdates();
        for (Timestamp model : timestamps) {
            sortedTimeStamps.remove(model);
        }
        sortedTimeStamps.endBatchedUpdates();
    }

    public void removeAll() {
        sortedTimeStamps.clear();
    }

    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView recycler_timestamp_category, recycler_timestamp_weekday, recycler_timestamp_day, recycler_timestamp_note, recycler_timestamp_time;
        // private ImageButton
        private LinearLayout left_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            recycler_timestamp_category = (TextView) itemView.findViewById(R.id.recycler_timestamp_category);
            recycler_timestamp_day = (TextView) itemView.findViewById(R.id.recycler_timestamp_day);
            recycler_timestamp_note = (TextView) itemView.findViewById(R.id.recycler_timestamp_note);
            recycler_timestamp_time = (TextView) itemView.findViewById(R.id.recycler_timestamp_time);
            //   mGPSpinButton = (ImageButton) itemView.findViewById(R.id.r);
            recycler_timestamp_weekday = (TextView) itemView.findViewById(R.id.recycler_timestamp_weekday);
            left_container = (LinearLayout) itemView.findViewById(R.id.left_container);
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
