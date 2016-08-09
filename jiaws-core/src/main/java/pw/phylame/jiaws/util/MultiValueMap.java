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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import lombok.NonNull;

public class MultiValueMap<K, V> implements Map<K, Collection<V>> {
    private final Map<K, Collection<V>> map;

    public MultiValueMap() {
        this(new HashMap<K, Collection<V>>());
    }

    public MultiValueMap(@NonNull Map<K, Collection<V>> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Collection<V> get(Object key) {
        return map.get(key);
    }

    public V getFirst(Object key) {
        Collection<V> c = get(key);
        return MiscUtils.isNotEmpty(c) ? MiscUtils.getFirst(c) : null;
    }

    @Override
    public Collection<V> put(K key, Collection<V> value) {
        return map.put(key, value);
    }

    public Collection<V> putOne(K key, V value) {
        Collection<V> prev = get(key), c = new LinkedList<>();
        c.add(value);
        put(key, c);
        return prev;
    }

    public void addOne(K key, V value) {
        Collection<V> c = get(key);
        if (c == null) {
            c = new LinkedList<>();
            put(key, c);
        }
        c.add(value);
    }

    @Override
    public Collection<V> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> m) {
        map.putAll(m);
    }

    public void update(Map<K, V> m) {
        for (Map.Entry<K, V> e : m.entrySet()) {
            addOne(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Collection<V>> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
