package pw.phylame.jiaws.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.NonNull;
import lombok.val;

public final class PropUtils {
    private PropUtils() {
    }

    public static Properties loadProperties(@NonNull String path, @NonNull Class<?> clazz) throws IOException {
        val in = clazz.getResourceAsStream(path);
        return in != null ? loadProperties(in) : new Properties();
    }

    public static Properties loadProperties(@NonNull String path, @NonNull ClassLoader classLoader) throws IOException {
        val in = ClassLoader.getSystemResourceAsStream(path);
        return in != null ? loadProperties(in) : new Properties();
    }

    public static Properties loadProperties(@NonNull InputStream in) throws IOException {
        Properties prop = new Properties();
        prop.load(in);
        return prop;
    }
}
