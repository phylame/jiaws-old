package pw.phylame.jiaws.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.NonNull;

public final class DateUtils {
    private DateUtils() {
    }

    public static final String GMT_FORMAT = "EEE, d MMM yyyy HH:mm:ss 'GMT'";

    private static final LazyValue<DateFormat> gmtFormatter = new LazyValue<>(new Provider<DateFormat>() {
        @Override
        public DateFormat provide() {
            return new SimpleDateFormat(GMT_FORMAT, Locale.ENGLISH);
        }

    });

    public static String toGMT(@NonNull Date date) {
        return gmtFormatter.get().format(date);
    }

    public static Date forGMT(@NonNull String str) throws ParseException {
        return gmtFormatter.get().parse(str);
    }

}
