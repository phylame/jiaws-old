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

import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;

public final class IOUtils {
    private IOUtils() {
    }

    public static final int DEFAULT_BUFFER_SIZE = 8190;

    public static String readLine(@NonNull InputStream in) throws IOException {
        StringBuilder b = new StringBuilder();
        int ch;
        while ((ch = in.read()) != -1) {
            b.append(ch);
            if (ch == '\n') {
                break;
            }
        }
        return b.toString();
    }
}
