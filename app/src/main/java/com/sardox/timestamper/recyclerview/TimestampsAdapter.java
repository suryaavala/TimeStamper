package com.sardox.timestamper.recyclerview;


import android.content.Context;

import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;
import com.sardox.timestamper.utils.ActionType;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.TimestampIcon;
import com.sardox.timestamper.utils.UserAction;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;


public class TimestampsAdapter extends RecyclerView.Adapter<TimestampsAdapter.MyViewHolder> {

    private SortedList<Timestamp> sortedTimeStamps;
    private List<Category> categories;
    private DisplayMetrics displayMetrics;
    private AppSettings appSettings;
    private List<TimestampIcon> icons;
    private Context context;
    private Consumer<UserAction> userActionCallback;
    private PeriodFormatter periodFormatter = PeriodFormat.getDefault();

    public TimestampsAdapter(List<Category> categories, DisplayMetrics displayMetrics, List<TimestampIcon> icons, Context context, Consumer<UserAction> userActionCallback, AppSettings appSettings) {
        this.userActionCallback = userActionCallback;
        this.context = context;
        this.appSettings = appSettings;
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
                if (position>0) {
                    // neighbor show be updated as well to update it's delta
                    notifyItemRangeChanged(position - 1, 1);
                }
            }

            @Override
            public boolean areContentsTheSame(Timestamp oldItem, Timestamp newItem) {
                return false;
            }

            @Override
            public boolean areItemsTheSame(Timestamp item1, Timestamp item2) {
                return item1.equals(item2);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
                if (position>0) {
                    // neighbor show be updated as well to update it's delta
                    notifyItemRangeChanged(position - 1, 1);
                }
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    public void updateAppSettings(AppSettings appSettings){
        this.appSettings = appSettings;
    }

    @Override
    public TimestampsAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_item, viewGroup,
                        false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final TimestampsAdapter.MyViewHolder holder, int position) {
        final int w = displayMetrics.widthPixels;
        holder.left_container.setMinimumWidth(w);

        Timestamp timestamp = sortedTimeStamps.get(position);
        Calendar calendar = Calendar.getInstance();
        TimeZone localTZ = calendar.getTimeZone();
        calendar.setTimeInMillis(timestamp.getTimestamp().toMilliseconds());

        String format;

        if (appSettings.shouldUse24hrFormat()) {
            if (appSettings.shouldShowMillis()) {
                format = "HH:mm:ss.SSS";
            } else format = "HH:mm:ss";
        } else {
            if (appSettings.shouldShowMillis()) {
                format = "hh:mm:ss.SSS a";
            } else format = "hh:mm:ss a";
        }

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

        JetUUID category_id = timestamp.getCategoryId();

        for (Category category : categories) {
            if (category.getCategoryID().equals(category_id)) {
                holder.recycler_timestamp_category.setImageDrawable(ContextCompat.getDrawable(context, icons.get(category.getIcon_id()).getDrawable_id()));
                break;
            }
        }
        if (position<sortedTimeStamps.size()-1) {
            Timestamp nexTimestamp = sortedTimeStamps.get(position+1);
            Period period = new Period(nexTimestamp.getTimestamp().toMilliseconds(),timestamp.getTimestamp().toMilliseconds());
            holder.recycler_timestamp_delay.setText(periodFormatter.print(period));
        } else {
            holder.recycler_timestamp_delay.setText("");
        }

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

    public void add(List<Timestamp> timestamps) {
        sortedTimeStamps.addAll(timestamps);
    }

    public void add(Collection<Timestamp> values) {
        sortedTimeStamps.addAll(values);
    }

    public void remove(Timestamp timestamp) {
        sortedTimeStamps.remove(timestamp);
    }

    public void removeAll() {
        sortedTimeStamps.clear();
    }

    public void updateTimestamp(Timestamp updatedTimestamp) {
        for (int i = 0; i < sortedTimeStamps.size(); i++) {
            if (sortedTimeStamps.get(i).getIdentifier().equals(updatedTimestamp.getIdentifier())) {
                sortedTimeStamps.updateItemAt(i, updatedTimestamp);
                return;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView recycler_timestamp_weekday, recycler_timestamp_day, recycler_timestamp_note, recycler_timestamp_time, recycler_timestamp_button_menu, recycler_timestamp_delay;
        private LinearLayout left_container;
        private ImageView recycler_timestamp_category;

        MyViewHolder(View itemView) {
            super(itemView);

            recycler_timestamp_category = (ImageView) itemView.findViewById(R.id.recycler_timestamp_category);
            recycler_timestamp_day = (TextView) itemView.findViewById(R.id.recycler_timestamp_day);
            recycler_timestamp_note = (TextView) itemView.findViewById(R.id.recycler_timestamp_note);
            recycler_timestamp_time = (TextView) itemView.findViewById(R.id.recycler_timestamp_time);
            recycler_timestamp_weekday = (TextView) itemView.findViewById(R.id.recycler_timestamp_weekday);
            recycler_timestamp_button_menu = (TextView) itemView.findViewById(R.id.recycler_timestamp_button_menu);
            recycler_timestamp_delay = (TextView) itemView.findViewById(R.id.in_between);
            left_container = (LinearLayout) itemView.findViewById(R.id.left_container);

            recycler_timestamp_button_menu.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.recycler_timestamp_button_menu: {
                    showPopup(v, sortedTimeStamps.get(getAdapterPosition()));
                    break;
                }
            }
        }
    }

    private void showPopup(View v, final Timestamp timestamp) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.options_menu);
        if (timestamp.getPhysicalLocation().equals(PhysicalLocation.Default)) {
            MenuItem map_to_menu_item = popup.getMenu().findItem(R.id.recycler_menu_map_to);
            if (map_to_menu_item != null) map_to_menu_item.setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recycler_menu_edit_date:
                        userActionCallback.accept(new UserAction(ActionType.EDIT_DATE, timestamp));
                        break;
                    case R.id.recycler_menu_edit_time:
                        userActionCallback.accept(new UserAction(ActionType.EDIT_TIME, timestamp));
                        break;
                    case R.id.recycler_menu_edit_note:
                        userActionCallback.accept(new UserAction(ActionType.EDIT_NOTE, timestamp));
                        break;
                    case R.id.recycler_menu_map_to:
                        userActionCallback.accept(new UserAction(ActionType.MAP_TO, timestamp));
                        break;
                    case R.id.recycler_menu_move:
                        userActionCallback.accept(new UserAction(ActionType.CHANGE_CATEGORY, timestamp));
                        break;
                    case R.id.recycler_menu_remove:
                        userActionCallback.accept(new UserAction(ActionType.REMOVE, timestamp));
                        break;
                    case R.id.recycler_menu_share:
                        userActionCallback.accept(new UserAction(ActionType.SHARE, timestamp));
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    private static final Comparator<Timestamp> TIMESTAMP_COMPARATOR_NEW_TOP = new Comparator<Timestamp>() {
        @Override
        public int compare(Timestamp a, Timestamp b) {
            return b.getTimestamp().compareTo(a.getTimestamp());
        }
    };
}
