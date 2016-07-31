/*
 * Copyright 2014-2016 Peng Wan <phylame@163.com>
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

package pw.phylame.jiaws.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.NonNull;
import pw.phylame.jiaws.util.Enumerations;

public class AttributeSupport {
    private final Map<String, Object> attributes;

    public AttributeSupport() {
        this(new HashMap<String, Object>());
    }

    public AttributeSupport(@NonNull Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Object getAttribute(@NonNull String name) {
        checkAccessible();
        return attributes.get(name);
    }

    protected final int getAttributeSize() {
        checkAccessible();
        return attributes.size();
    }

    protected final Set<String> getAttributeKeys() {
        checkAccessible();
        return attributes.keySet();
    }

    public Enumeration<String> getAttributeNames() {
        return Enumerations.forCollection(getAttributeKeys());
    }

    public void setAttribute(@NonNull String name, Object value) {
        checkAccessible();
        if (value == null) {
            removeAttribute(name);
        } else {
            Object prev = attributes.put(name, value);
            if (prev == null) {
                attributeAdded(name, value);
            } else {
                attributeReplaced(name, prev);
            }
        }
    }

    public void removeAttribute(@NonNull String name) {
        checkAccessible();
        Object prev = attributes.remove(name);
        if (prev != null) {
            attributeRemoved(name, prev);
        }
    }

    /**
     * Checks the attributes is accessible or not.
     *
     * @throws RuntimeException
     *             if is not accessible
     */
    protected void checkAccessible() throws RuntimeException {

    }

    protected void attributeAdded(String name, Object value) {

    }

    protected void attributeRemoved(String name, Object value) {

    }

    protected void attributeReplaced(String name, Object value) {

    }
}
