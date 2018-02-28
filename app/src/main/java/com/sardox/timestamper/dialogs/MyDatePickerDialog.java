package com.sardox.timestamper.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.TimestampFormat;
import com.sardox.timestamper.utils.Consumer;

import java.util.Calendar;

public class MyDatePickerDialog {

    public MyDatePickerDialog(Context context, final Timestamp timestamp, final Consumer<JetTimestamp> onDateSelected) {
        final int oldYear = timestamp.format(TimestampFormat.Year);
        final int oldMonth = timestamp.format(TimestampFormat.Month);
        final int oldDay = timestamp.format(TimestampFormat.Day);

        DatePickerDialog jetDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int newYear, int newMonth, int newDay) {
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(timestamp.getTimestamp().toMilliseconds());
                c.set(newYear, newMonth, newDay);
                JetTimestamp updatedDate = JetTimestamp.fromMilliseconds(c.getTimeInMillis());
                c.clear();
                onDateSelected.accept(updatedDate);
            }
        }, oldYear, oldMonth, oldDay);
        jetDatePicker.show();
    }

}

