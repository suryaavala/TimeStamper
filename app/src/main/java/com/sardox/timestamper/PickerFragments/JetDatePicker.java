package com.sardox.timestamper.PickerFragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.DatePicker;

import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.TimestampFormat;
import com.sardox.timestamper.utils.Consumer;


public class JetDatePicker extends DatePickerDialog implements DatePickerDialog.OnDateSetListener{
    private Consumer<JetTimestamp> callback;

    public JetDatePicker(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    public void setCallback(Consumer<JetTimestamp> callback) {
        this.callback = callback;
    }

//    @Override
//    public void setOnDateSetListener(@Nullable OnDateSetListener listener) {
//        super.setOnDateSetListener(this);
//    }

//    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//        callback.accept(JetTimestamp.now());
//    }

    public void updateDate(Timestamp timestamp) {
        int year  = timestamp.format(TimestampFormat.Year);
        int month = timestamp.format(TimestampFormat.Month);
        int day =  timestamp.format(TimestampFormat.Day);

        updateDate(year,month,day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.v("srdx", "onDateSet Picker");
    }
}
