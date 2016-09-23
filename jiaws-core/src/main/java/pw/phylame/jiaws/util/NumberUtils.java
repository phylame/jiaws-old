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

import pw.phylame.ycl.util.StringUtils;

/**
 * Utilities for number.
 * <p>
 * Created: 2016-09-23 11:16:41
 *
 * @author PW[phylame@163.com]
 */
public final class NumberUtils {
    private NumberUtils() {
    }

    public static int valueOf(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if (ch >= 'a' && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }

    public static int parseInt(String str, int defaultValue) {
        return parseInt(str, 10, defaultValue);
    }

    public static int parseInt(String str, int radix, int defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str, radix);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long parseLong(String str, long fallback) {
        return parseLong(str, 10, fallback);
    }

    public static long parseLong(String str, int radix, long fallback) {
        if (StringUtils.isEmpty(str)) {
            return fallback;
        }
        try {
            return Long.parseLong(str, radix);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

}
