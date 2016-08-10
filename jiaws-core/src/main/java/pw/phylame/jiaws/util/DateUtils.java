/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package pw.phylame.jiaws.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.NonNull;
import pw.phylame.jiaws.util.values.LazyValue;

public final class DateUtils {
    private DateUtils() {
    }

    public static final String RFC1123_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    public static final String RFC1036_FORMAT = "EEEEEE, dd-MMM-yy HH:mm:ss z";

    public static final String ANSIC_FORMAT = "EEE MMM d HH:mm:ss z yyyy";

    private static final LazyValue<DateFormat> rfc1123Formatter = new LazyValue<>(new Provider<DateFormat>() {
        @Override
        public DateFormat provide() {
            return new SimpleDateFormat(RFC1123_FORMAT, Locale.ENGLISH);
        }
    });

    private static final LazyValue<DateFormat> rfc1036Formatter = new LazyValue<>(new Provider<DateFormat>() {
        @Override
        public DateFormat provide() {
            return new SimpleDateFormat(RFC1036_FORMAT, Locale.ENGLISH);
        }
    });

    private static final LazyValue<DateFormat> ansicFormatter = new LazyValue<>(new Provider<DateFormat>() {
        @Override
        public DateFormat provide() {
            return new SimpleDateFormat(ANSIC_FORMAT, Locale.ENGLISH);
        }
    });

    public static String toRFC1123(@NonNull Date date) {
        return rfc1123Formatter.get().format(date);
    }

    public static String toRFC822(@NonNull Date date) {
        return rfc1123Formatter.get().format(date);
    }

    public static Date forRFC1123(@NonNull String str) throws ParseException {
        return rfc1123Formatter.get().parse(str);
    }

    public static Date forRFC822(@NonNull String str) throws ParseException {
        return rfc1123Formatter.get().parse(str);
    }

    public static String toRFC1036(@NonNull Date date) {
        return rfc1036Formatter.get().format(date);
    }

    public static String toRFC850(@NonNull Date date) {
        return rfc1036Formatter.get().format(date);
    }

    public static Date forRFC1036(@NonNull String str) throws ParseException {
        return rfc1036Formatter.get().parse(str);
    }

    public static Date forRFC850(@NonNull String str) throws ParseException {
        return rfc1036Formatter.get().parse(str);
    }

    public static String toANSIC(@NonNull Date date) {
        return ansicFormatter.get().format(date);
    }

    public static Date forANSIC(@NonNull String str) throws ParseException {
        return ansicFormatter.get().parse(str);
    }

    public static Date parseDate(String str, Date defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return forRFC1123(str);
        } catch (ParseException e) {
        }
        try {
            return forRFC1036(str);
        } catch (ParseException e) {
        }
        try {
            return forANSIC(str);
        } catch (ParseException e) {
            return defaultValue;
        }
    }
}
