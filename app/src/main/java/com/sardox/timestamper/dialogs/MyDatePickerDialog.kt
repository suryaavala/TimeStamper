package com.sardox.timestamper.dialogs

import android.app.DatePickerDialog
import android.content.Context
import com.sardox.timestamper.objects.Timestamp
import com.sardox.timestamper.types.JetTimestamp
import com.sardox.timestamper.types.TimestampFormat
import java.util.*

class MyDatePickerDialog(context: Context, timestamp: Timestamp, onDateSelected: (jetTimestamp: JetTimestamp) -> Unit) {

    init {
        val oldYear = timestamp.format(TimestampFormat.Year)
        val oldMonth = timestamp.format(TimestampFormat.Month)
        val oldDay = timestamp.format(TimestampFormat.Day)

        val jetDatePicker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, newYear, newMonth, newDay ->
            val c = Calendar.getInstance()
            c.timeInMillis = timestamp.timestamp.toMilliseconds()
            c.set(newYear, newMonth, newDay)
            val updatedDate = JetTimestamp.fromMilliseconds(c.timeInMillis)
            c.clear()
            onDateSelected.invoke(updatedDate)
        }, oldYear, oldMonth, oldDay)
        jetDatePicker.show()
    }
}