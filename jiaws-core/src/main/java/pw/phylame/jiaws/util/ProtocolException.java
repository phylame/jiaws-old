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

import lombok.Getter;
import lombok.NonNull;

/**
 * Exception for request with bad protocol.
 *
 */
public class ProtocolException extends JiawsException {
    private static final long serialVersionUID = 646686496222492271L;

    /**
     * The name of protocol.
     */
    @Getter
    @NonNull
    private String protocol;

    public ProtocolException(String protocol) {
        super();
        this.protocol = protocol;
    }

    public ProtocolException(String message, String protocol) {
        super(message);
        this.protocol = protocol;
    }

}
