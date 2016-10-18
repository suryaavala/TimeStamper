package com.sardox.timestamper.pickers;

import android.app.Dialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sardox on 8/8/2016.
 */
public  class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static FragmentManager fragmentmanager;
    public static long time;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        final Calendar c = Calendar.getInstance();

        c.setTimeInMillis(time);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Log.e("aaa", "onDateSet!!!");

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        int hr = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);

        c.set(view.getYear(),view.getMonth(),view.getDayOfMonth(),hr,min,sec);
        time=c.getTimeInMillis();





        String format = "yyyy.MM.dd G 'at' HH:mm:ss z";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String hhmmss = sdf.format(c.getTime());
        Log.e("aaa", hhmmss);

        TimePickerFragment.time =time;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(fragmentmanager, "Change the time");

    }
}