package com.sardox.timestamper.pickers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import com.sardox.timestamper.MainActivity;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sardox on 8/8/2016.
 */
public  class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public static long time;
    public static int pos;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int sec = c.get(Calendar.SECOND);



        c.set(year,month,day,view.getCurrentHour(),view.getCurrentMinute(), sec);


        time=c.getTimeInMillis();
        String format = "yyyy.MM.dd G 'at' HH:mm:ss z";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String hhmmss = sdf.format(c.getTime());
        Log.e("aaa", hhmmss);


        MainActivity.adapter.mStampsList.get(pos).setTime(time);
        MainActivity.adapter.notifyDataSetChanged();
        MainActivity.adapter.mainActivity.filterList();
        // MainActivity.adapter.




    }
}

