package http.parser.header;

import static http.Base.*;

public class DateParser {

    public static class Date {
        public final String value;

        public Date(String value) {
            this.value = value;
        }
    }

    enum DayName { Mon, Tue, Wed, Thu, Fri, Sat, Sun}

    public static String DAY_NAME(ByteStream bs, Buffer bfr) {
        bfr.reset();
        bfr.push(bs.advance()); bfr.push(bs.advance()); bfr.push(bs.advance());

        var value = bfr.toStringAndReset();

        try {
            DayName.valueOf(value);
        } catch (IllegalArgumentException _) {
            throw new RuntimeException("Expected day name");
        }

        return value;
    }

    enum MonthsName {Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec}

    public static String MONTH(ByteStream bs, Buffer bfr) {
        bfr.reset();
        bfr.push(bs.advance()); bfr.push(bs.advance()); bfr.push(bs.advance());

        var value = bfr.toStringAndReset();

        try {
            MonthsName.valueOf(value);
        } catch (IllegalArgumentException _) {
            throw new RuntimeException("Expected month name");
        }

        return value;
    }

    public static String GMT(ByteStream bs) {
        BYTE(bs, 'G');BYTE(bs, 'M');BYTE(bs, 'T');return "GMT";
    }

    public static int NDIGIT(ByteStream bs, int N) {
        int value = 0;
        var exp = 1;

        for (var i = 0; i < N; i++) {
            value += (DIGIT(bs) - '0') * exp;
            exp *= 10;
        }

        return value;
    }

    public static String DATE1(ByteStream bs, Buffer bfr) {
        var day = NDIGIT(bs, 2); BYTE(bs, ' ');
        var month = MONTH(bs, bfr); BYTE(bs, ' ');
        bfr.reset();
        var year = NDIGIT(bs, 4);

        return day + month + year;
    }

    public static String TIME_OF_DAY(ByteStream bs) {
        var hour = NDIGIT(bs, 2); BYTE(bs, ':');
        if (hour >= 24) throw new RuntimeException("Hour should be less then 24");

        var minute = NDIGIT(bs, 2); BYTE(bs, ':');
        if (minute >= 60) throw new RuntimeException("Minute should be less then 60");

        var second = NDIGIT(bs, 2);
        if (second >= 60) throw new RuntimeException("Second should be less then 60");

        return hour + ":" + minute + ":" + second;
    }

    public static String IMF_FIX_DATE(ByteStream bs, Buffer bfr) {
        var dayName = DAY_NAME(bs, bfr); BYTE(bs, ','); BYTE(bs, ' ');
        var date1 = DATE1(bs, bfr); BYTE(bs, ' ');
        var timeOfDay = TIME_OF_DAY(bs); BYTE(bs, ' ');

        return dayName + ", " + date1 + " " + timeOfDay + " " + GMT(bs);
    }

    public static Date DATE(ByteStream bs, Buffer bfr) {
        bfr.reset();
        return new Date(IMF_FIX_DATE(bs, bfr));
    }
}
