/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package pw.phylame.jiaws.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import pw.phylame.ycl.util.Provider;
import pw.phylame.ycl.value.Lazy;

public final class HttpUtils {
    private HttpUtils() {
    }

    public static final String HTTP_STATUS_FILE = "/pw/phylame/jiaws/http/status.properties";

    private static final Lazy<Properties> httpStatus = new Lazy<>(new Provider<Properties>() {
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
}
