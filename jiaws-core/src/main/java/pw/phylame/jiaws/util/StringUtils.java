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

import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {
    private StringUtils() {
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static String getFirstPartOf(@NonNull String str, @NonNull String sep) {
        int index = str.indexOf(sep);
        return index < 0 ? str : str.substring(0, index);
    }

    public static String getFirstPartOf(@NonNull String str, char sep) {
        int index = str.indexOf(sep);
        return index < 0 ? str : str.substring(0, index);
    }

    public static String getSecondPartOf(@NonNull String str, @NonNull String sep) {
        int index = str.indexOf(sep);
        return index < 0 ? str : str.substring(index + sep.length());
    }

    public static String getSecondPartOf(@NonNull String str, char sep) {
        int index = str.indexOf(sep);
        return index < 0 ? str : str.substring(index + 1);
    }

    public static String getValueOfName(String str, String name, String sep, boolean ignoreCase) {
        for (String part : str.split(sep)) {
            int index = part.trim().indexOf('=');
            if (index != -1) {
                val n = part.substring(0, index);
                if (ignoreCase && n.equalsIgnoreCase(name) || n.equals(name)) {
                    return part.substring(index + 1);
                }
            }
        }
        return null;
    }

    public static String[] getValuesOfName(String str, String name, String sep, boolean ignoreCase) {
        List<String> result = new ArrayList<>();
        for (String part : str.split(sep)) {
            int index = part.trim().indexOf('=');
            if (index != -1) {
                val n = part.substring(0, index);
                if (ignoreCase && n.equalsIgnoreCase(name) || n.equals(name)) {
                    result.add(part.substring(index + 1));
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
