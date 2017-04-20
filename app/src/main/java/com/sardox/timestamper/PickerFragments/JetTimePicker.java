package com.sardox.timestamper.PickerFragments;

import android.app.TimePickerDialog;
import android.content.Context;

import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.TimestampFormat;


public class JetTimePicker extends TimePickerDialog {
    public JetTimePicker(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
    }

    @Override
    public void updateTime(int hourOfDay, int minuteOfHour) {
        super.updateTime(hourOfDay, minuteOfHour);
    }

    public void updateDate(Timestamp timestamp) {
        int minuteOfHour = timestamp.format(TimestampFormat.MIN);
        int hourOfDay = timestamp.format(TimestampFormat.HRS24);

        updateTime(hourOfDay, minuteOfHour);
    }
}
