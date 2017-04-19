package com.sardox.timestamper.recyclerview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.util.SortedList;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sardox.timestamper.PickerFragments.DatePickerFragment;
import com.sardox.timestamper.PickerFragments.TimePickerFragment;
import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetTimestampFormat;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.utils.TimestampIcon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private SortedList<Timestamp> sortedTimeStamps; // filteredTimestamps
    private List<Category> categories;
    private DisplayMetrics displayMetrics;
    private AppSettings appSettings;
    private List<TimestampIcon> icons;
    private Context context;

    public MyRecyclerViewAdapter(List<Category> categories, DisplayMetrics displayMetrics, List<TimestampIcon> icons, Context context) {

        this.context = context;
        this.categories = categories;
        this.displayMetrics = displayMetrics;
        this.icons = icons;

        sortedTimeStamps = new SortedList<>(Timestamp.class, new SortedList.Callback<Timestamp>() {
            @Override
            public int compare(Timestamp o1, Timestamp o2) {
                return TIMESTAMP_COMPARATOR_NEW_TOP.compare(o1, o2);
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
    public void onBindViewHolder(final MyRecyclerViewAdapter.MyViewHolder holder, int position) {
        final int w = displayMetrics.widthPixels;
        holder.left_container.setMinimumWidth(w);
        Log.e("stamper", "setMinimumWidth: " + w);
        Log.e("stamper", "left_container w: " + holder.left_container.getWidth());

        Timestamp timestamp = sortedTimeStamps.get(position);

        JetTimestamp.now().toString(Locale.getDefault(), Calendar.getInstance().getTimeZone(), JetTimestampFormat.LongDateTime);

        Calendar calendar = Calendar.getInstance();
        TimeZone localTZ = calendar.getTimeZone();

        calendar.setTimeInMillis(timestamp.getTimestamp().toMilliseconds());
        String format = "hh:mm:ss.SSS a";

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

        holder.recycler_timestamp_category.setImageDrawable(ContextCompat.getDrawable(context, icons.get(categories.get(timestamp.getCategoryId()).getIcon_id()).getDrawable_id()));
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView recycler_timestamp_weekday, recycler_timestamp_day, recycler_timestamp_note, recycler_timestamp_time, recycler_timestamp_button_menu;
        // private ImageButton
        private LinearLayout left_container;
        private ImageView recycler_timestamp_category;

        public MyViewHolder(View itemView) {
            super(itemView);


            recycler_timestamp_category = (ImageView) itemView.findViewById(R.id.recycler_timestamp_category);
            recycler_timestamp_day = (TextView) itemView.findViewById(R.id.recycler_timestamp_day);
            recycler_timestamp_note = (TextView) itemView.findViewById(R.id.recycler_timestamp_note);
            recycler_timestamp_time = (TextView) itemView.findViewById(R.id.recycler_timestamp_time);
            //   mGPSpinButton = (ImageButton) itemView.findViewById(R.id.r);
            recycler_timestamp_weekday = (TextView) itemView.findViewById(R.id.recycler_timestamp_weekday);
            recycler_timestamp_button_menu = (TextView) itemView.findViewById(R.id.recycler_timestamp_button_menu);
            left_container = (LinearLayout) itemView.findViewById(R.id.left_container);

            recycler_timestamp_button_menu.setOnClickListener(this);

        }



//            if (v.getId() == mNameTextView.getId()) {
//                DatePickerFragment.time = mStampsList.get(getAdapterPosition()).getTime();
//                TimePickerFragment.pos = getAdapterPosition();
//                DatePickerFragment.fragmentmanager = fragmentmanager;
//                DialogFragment newFragment = new DatePickerFragment();
//                newFragment.show(fragmentmanager, "Change the date");
//            }

        @Override
        public void onClick(View v) {
            // int a = getAdapterPosition();

            switch (v.getId()) {
                case R.id.recycler_timestamp_button_menu: {
                    show_popup(v);
                    break;
                }
            }
        }


        private void show_DatePicker() {
//                DatePickerFragment.time = mStampsList.get(getAdapterPosition()).getTime();
//                TimePickerFragment.pos = getAdapterPosition();
//                DatePickerFragment.fragmentmanager = fragmentmanager;
//                DialogFragment newFragment = new DatePickerFragment();
//                newFragment.show(fragmentmanager, "Change the date");
        }


          private void showSpinner(View v, final int pos) {
        } // spinner dialog -- change items category
        

        public void showDialog(View v, final int pos) {
        }  // input dialog -- for a note

    }

    private void show_popup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.options_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recycler_menu_edit_date:
                        //handle menu1 click
                        break;
                    case R.id.recycler_menu_edit_note:
                        //handle menu2 click
                        break;
                    case R.id.recycler_menu_edit_time:
                        //handle menu3 click
                        break;
                    case R.id.recycler_menu_map_to:
                        //handle menu3 click
                        break;
                    case R.id.recycler_menu_move:
                        //handle menu3 click
                        break;
                    case R.id.recycler_menu_remove:
                        //handle menu3 click
                        break;
                    case R.id.recycler_menu_share:
                        //handle menu3 click
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();

    }
    private static final Comparator<Timestamp> TIMESTAMP_COMPARATOR_NEW_TOP = new Comparator<Timestamp>() {
        @Override
        public int compare(Timestamp a, Timestamp b) {
            return a.getTimestamp().compareTo(b.getTimestamp());
        }
    };
}
