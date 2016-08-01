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

import java.util.Properties;

import lombok.NonNull;

public final class FileUtils {
    private FileUtils() {

    }

    public static final String MIME_MAP_FILE = "/pw/phylame/jiaws/mime.properties";

    private static final LazyValue<Properties> mimeMap = new LazyValue<>(new Provider<Properties>() {
        @Override
        public Properties provide() throws Exception {
            return PropUtils.loadProperties(MIME_MAP_FILE, FileUtils.class);
        }
    });

    /**
     * Gets MIME by file extension name.
     * 
     * @param extension
     *            the extension name
     * @return the MIME or <code>null</code> if unknown
     */
    public static String getMimeType(@NonNull String extension) {
        return mimeMap.get().getProperty(extension);
    }
}
