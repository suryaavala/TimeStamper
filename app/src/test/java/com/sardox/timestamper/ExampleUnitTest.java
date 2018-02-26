package com.sardox.timestamper;

import com.sardox.timestamper.objects.QuickNote;
import com.sardox.timestamper.objects.QuickNoteList;
import com.sardox.timestamper.types.JetTimestamp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        QuickNote quickNote1 = new QuickNote(JetTimestamp.fromMilliseconds(1000), "two");
        QuickNote quickNote2 = new QuickNote(JetTimestamp.fromMilliseconds(3000), "five");
        QuickNote quickNote3 = new QuickNote(JetTimestamp.fromMilliseconds(2000), "TWO");

        QuickNoteList list = new QuickNoteList();
        list.add(quickNote1);
        list.add(quickNote2);
        list.add(quickNote3);
        assertEquals(list.size(), 2);
    }

    @Test
    public void addition_isCorrect2() throws Exception {
        QuickNote quickNote1 = new QuickNote(JetTimestamp.fromMilliseconds(1000), "ONE");
        QuickNote quickNote2 = new QuickNote(JetTimestamp.fromMilliseconds(2000), "TWO");
        QuickNote quickNote3 = new QuickNote(JetTimestamp.fromMilliseconds(3000), "THREE");
        QuickNote quickNote4 = new QuickNote(JetTimestamp.fromMilliseconds(4000), "FOUR");
        QuickNote quickNote5 = new QuickNote(JetTimestamp.fromMilliseconds(5000), "FIVE");
        QuickNote quickNote6 = new QuickNote(JetTimestamp.fromMilliseconds(6000), "SIX");
        QuickNoteList list = new QuickNoteList();
        list.addNote(quickNote1);
        list.addNote(quickNote2);
        list.addNote(quickNote3);
        list.addNote(quickNote4);
        list.addNote(quickNote5);
        list.addNote(quickNote6);

        QuickNote quickNote7 = new QuickNote(JetTimestamp.fromMilliseconds(7000), "two");
        list.addNote(quickNote7);
        QuickNote quickNote8 = new QuickNote(JetTimestamp.fromMilliseconds(8000), "two");
        list.addNote(quickNote8);
        assertEquals(4, 2 + 2);
    }
}