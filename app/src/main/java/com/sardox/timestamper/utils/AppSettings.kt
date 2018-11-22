package com.sardox.timestamper.utils

import com.sardox.timestamper.objects.QuickNoteList
import com.sardox.timestamper.types.JetUUID

class AppSettings {

    var use24hrFormat = false
    var showNoteAddDialog = false
    var shouldUseGps = false
    var shouldUseQuickNotes = true
    var shouldShowMillis = false
    var shouldShowKeyboardInAddNote = false
    var shouldUseDarkTheme = false
    var quickNotes: QuickNoteList = QuickNoteList()

    companion object {
        val NO_DEFAULT_CATEGORY = JetUUID.fromString("3f02dce5-d2de-4d3c-96d6-de0f6123baa8")
    }
}