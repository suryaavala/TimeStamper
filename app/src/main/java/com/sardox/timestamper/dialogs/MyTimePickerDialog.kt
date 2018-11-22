package com.sardox.timestamper.dialogs

import android.app.TimePickerDialog
import android.content.Context
import com.sardox.timestamper.objects.Timestamp
import com.sardox.timestamper.types.JetDuration
import com.sardox.timestamper.types.JetTimestamp
import com.sardox.timestamper.types.TimestampFormat


class MyTimePickerDialog(context: Context, timestamp: Timestamp, onTimeSelected: (jetTimestamp: JetTimestamp) -> Unit, shouldUse24hrFormat: Boolean) {

    init {
        val oldHrs24 = timestamp.format(TimestampFormat.HRS24)
        val oldMin = timestamp.format(TimestampFormat.MIN)
        val oldDifInMillis = ((oldHrs24 * 60 + oldMin) * 60 * 1000).toLong()

        val timePickerDialog = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, new_hr, new_min ->
            val newDifInMillis = ((new_hr * 60 + new_min) * 60 * 1000).toLong()
            val delta = newDifInMillis - oldDifInMillis
            var updatedDate = timestamp.timestamp
            updatedDate = updatedDate.add(JetDuration.fromMilliseconds(delta))
            onTimeSelected.invoke(updatedDate)
        }, oldHrs24, oldMin, shouldUse24hrFormat)
        timePickerDialog.show()
    }
}