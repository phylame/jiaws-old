package pw.phylame.jiaws.util;

import lombok.NonNull;

public final class StringUtils {
    private StringUtils() {
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
}
