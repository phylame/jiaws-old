package pw.phylame.jiaws.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.NonNull;

public final class HttpUtils {
    private HttpUtils() {
    }

    public static final String HTTP_STATUS_FILE = "/http-status.properties";

    private static final LazyValue<Properties> httpStatus = new LazyValue<>(new Provider<Properties>() {
        @Override
        public Properties provide() throws IOException {
            return loadHttpStatus();
        }
    });

    private static Properties loadHttpStatus() throws IOException {
        Properties prop = new Properties();
        try (InputStream in = HttpUtils.class.getResourceAsStream(HTTP_STATUS_FILE)) {
            prop.load(in);
        }
        return prop;
    }

    public static String getStatusReason(int code) {
        return httpStatus.get().getProperty(Integer.toString(code), "Unknow error");
    }

    public static String getCharsetForContentType(@NonNull String type) {
        for (String part : type.split(";")) {
            int index = part.indexOf('=');
            if (index != -1 && part.substring(0, index).equalsIgnoreCase("charset")) {
                return part.substring(index + 1);
            }
        }
        return null;
    }
}
