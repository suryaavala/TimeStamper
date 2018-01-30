package com.sardox.timestamper.types;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class JetTimestamp {
    private static final SimpleDateFormat iso8601Format = createISO8601Format();

    /**
     * The milliseconds from 1970-01-01T00:00:00.000Z
     */
    private final long milliseconds;


    /**
     * 1970-01-01T00:00:00.000Z
     */
    public static final JetTimestamp Zero = new JetTimestamp();


    public static JetTimestamp now() {
        return fromMilliseconds(System.currentTimeMillis());
    }

    private JetTimestamp(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public JetTimestamp() {
        this.milliseconds = 0L;
    }


    public static JetTimestamp fromMilliseconds(long milliseconds) {
        return new JetTimestamp(milliseconds);
    }

    public static JetTimestamp fromSqlTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return Zero;
        }
        return fromMilliseconds(timestamp.getTime());
    }

    public static JetTimestamp fromString(String str) {
        if (str == null || "".equals(str)) {
            return Zero;
        }

        try {
            Date dateTime = iso8601Format.parse(str);
            return new JetTimestamp(dateTime.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public long toMilliseconds() {
        return milliseconds;
    }

    public boolean isZero() {
        return this.milliseconds == 0L;
    }

    public JetTimestamp add(JetDuration duration) {
        return new JetTimestamp(this.milliseconds + duration.toMilliseconds());
    }

    public JetTimestamp subtract(JetDuration duration) {
        return new JetTimestamp(this.milliseconds - duration.toMilliseconds());
    }

    public Timestamp toSqlTimestamp() {
        if (milliseconds == 0L)
            return null;
        return new Timestamp(milliseconds);
    }

    /**
     * ISO 8601 (ex. 1970-01-01T00:00:00.000Z)
     */
    @Override
    public String toString() {
        return toString(milliseconds);
    }


    public String toString(Locale locale, TimeZone timeZone, JetTimestampFormat format) {
        Date dt = new Date(milliseconds); // uses local timezone

        switch (format) {
            case ShortDate:
            case MediumDate:
            case LongDate:
                return toDateString(dt, locale, timeZone, format);
            case ShortTime:
            case MediumTime:
            case LongTime:
                return toTimeString(dt, locale, timeZone, format);
            case ShortDateTime:
            case MediumDateTime:
            case LongDateTime:
                return toDateString(dt, locale, timeZone, format) + " " + toTimeString(dt, locale, timeZone, format);
            default:
                throw new RuntimeException("Invalid specifier");
        }
    }

    public String toDateString(Locale locale, TimeZone timeZone, JetTimestampFormat format) {
        final Date dt = new Date(milliseconds); // uses local timezone
        return toDateString(dt, locale, timeZone, format);
    }

    public String toTimeString(Locale locale, TimeZone timeZone, JetTimestampFormat format) {
        final Date dt = new Date(milliseconds); // uses local timezone
        return toTimeString(dt, locale, timeZone, format);
    }

    private String toDateString(Date dt, Locale locale, TimeZone timeZone, JetTimestampFormat format) {
        int kind = getDateKind(format);
        DateFormat formatter = DateFormat.getDateInstance(kind, locale);
        formatter.setTimeZone(timeZone);
        return formatter.format(dt);
    }

    private String toTimeString(Date dt, Locale locale, TimeZone timeZone, JetTimestampFormat format) {
        int kind = getTimeKind(format);
        DateFormat formatter = DateFormat.getTimeInstance(kind, locale);
        formatter.setTimeZone(timeZone);
        return formatter.format(dt);
    }

    private int getDateKind(JetTimestampFormat format) {
        switch (format) {
            case ShortDate:
            case ShortDateTime:
                return DateFormat.SHORT;
            case MediumDate:
            case MediumDateTime:
                return DateFormat.MEDIUM;
            case LongDate:
            case LongDateTime:
                return DateFormat.LONG;
            default:
                throw new RuntimeException("Invalid specifier");
        }
    }

    private int getTimeKind(JetTimestampFormat format) {
        switch (format) {
            case ShortTime:
            case ShortDateTime:
                return DateFormat.SHORT;
            case MediumTime:
            case MediumDateTime:
                return DateFormat.MEDIUM;
            case LongTime:
            case LongDateTime:
                return DateFormat.LONG;
            default:
                throw new RuntimeException("Invalid specifier");
        }
    }

    private static SimpleDateFormat createISO8601Format() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    private static SimpleDateFormat createISO8601TZ_Format(Locale locale, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat;
    }

    private static String toString(long milliseconds) {
        return iso8601Format.format(new Date(milliseconds));
    }

    private static String toStringTZ(long milliseconds, Locale locale, TimeZone tz) {
        return createISO8601TZ_Format(locale, tz).format(new Date(milliseconds));
    }

    public String toString(Locale locale, TimeZone timeZone) {
        return toStringTZ(milliseconds, locale, timeZone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JetTimestamp that = (JetTimestamp) o;

        return milliseconds == that.milliseconds;
    }

    @Override
    public int hashCode() {
        return (int) (milliseconds ^ (milliseconds >>> 32));
    }

    public int compareTo(JetTimestamp timestamp) {
        long other = timestamp.milliseconds;
        return milliseconds < other ? -1 : (milliseconds == other ? 0 : 1);
    }
}
