package com.sardox.timestamper.dialogs;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetDuration;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.TimestampFormat;
import com.sardox.timestamper.utils.Consumer;


public class MyTimePickerDialog {

    public MyTimePickerDialog(Context context, final Timestamp timestamp, final Consumer<JetTimestamp> onTimeSelected, boolean shouldUse24hrFormat) {
        final int oldHrs24 = timestamp.format(TimestampFormat.HRS24);
        final int oldMin = timestamp.format(TimestampFormat.MIN);
        final long oldDifInMillis = (oldHrs24 * 60 + oldMin) * 60 * 1000;

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int new_hr, int new_min) {
                final long newDifInMillis = (new_hr * 60 + new_min) * 60 * 1000;
                long delta = newDifInMillis - oldDifInMillis;
                JetTimestamp updatedDate = timestamp.getTimestamp();
                updatedDate = updatedDate.add(JetDuration.fromMilliseconds(delta));
                onTimeSelected.accept(updatedDate);
            }
        }, oldHrs24, oldMin, shouldUse24hrFormat);
        timePickerDialog.show();
    }

}

