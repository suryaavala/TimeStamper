package com.sardox.timestamper.types;

import java.util.Locale;

public class JetDuration {
    private final long milliseconds;


    public static final JetDuration Zero = new JetDuration();

    private JetDuration(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public JetDuration() {
        this.milliseconds = 0;
    }


    public static JetDuration fromMilliseconds(long milliseconds) {
        return new JetDuration(milliseconds);
    }

    public static JetDuration fromSeconds(long seconds) {
        return fromMilliseconds(seconds * 1000L);
    }

    public static JetDuration fromMinutes(long minutes) {
        return fromSeconds(minutes * 60L);
    }

    public static JetDuration fromHours(long hours) {
        return fromMinutes(hours * 60L);
    }

    public static JetDuration fromDays(long days) {
        return fromHours(days * 24L);
    }

    public static JetDuration fromYears(long years) {
        return fromDays(years * 365L);
    }

    public static JetDuration fromString(String str) {
        if (str == null || "".equals(str)) {
            return Zero;
        }

        return new JetDuration(StrictMath.round(Double.parseDouble(str) * 1000.0));
    }

    public JetDuration add(JetDuration duration) {
        return new JetDuration(this.milliseconds + duration.toMilliseconds());
    }

    public JetDuration subtract(JetDuration duration) {
        return new JetDuration(this.milliseconds - duration.toMilliseconds());
    }

    public long toMilliseconds() {
        return milliseconds;
    }

    @Override
    public String toString() {
        return toString(milliseconds);
    }


    private static String toString(long milliseconds) {
        return String.format(Locale.ROOT, "%.3f", milliseconds / 1000.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JetDuration that = (JetDuration) o;

        return milliseconds == that.milliseconds;
    }

    @Override
    public int hashCode() {
        return (int) (milliseconds ^ (milliseconds >>> 32));
    }
}
