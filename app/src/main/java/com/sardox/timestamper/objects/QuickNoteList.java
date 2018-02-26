package com.sardox.timestamper.objects;

import java.util.TreeSet;

public class QuickNoteList extends TreeSet<QuickNote> {
    private final int quickNotesSize = 5;

    public QuickNoteList() {
    }

    public void addNote(QuickNote quickNote) {
        QuickNote duplicate = findDuplicate(quickNote);
        if (duplicate != null) {
            remove(duplicate);
        }
        add(quickNote);
        if (size() > quickNotesSize) {
            remove(last());
        }
    }

    /**
     * @return null if not found else returns QuickNote
     */
    private QuickNote findDuplicate(QuickNote quickNote) {
        for (QuickNote q : this) {
            if (quickNote.getNote().equalsIgnoreCase(q.getNote())) {
                return q;
            }
        }
        return null;
    }
}
