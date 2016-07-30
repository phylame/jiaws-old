/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
