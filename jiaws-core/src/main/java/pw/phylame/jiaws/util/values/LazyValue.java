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

package pw.phylame.jiaws.util.values;

import lombok.NonNull;
import pw.phylame.jiaws.util.Provider;

public class LazyValue<T> extends Value<T> {
    private boolean initialized = false;

    private Provider<T> provider;

    private T fallback;

    public LazyValue(@NonNull Provider<T> provider) {
        this(provider, null);
    }

    public LazyValue(@NonNull Provider<T> provider, T fallback) {
        this.provider = provider;
        this.fallback = fallback;
    }

    @Override
    public T get() {
        if (!initialized) {
            initValue();
        }
        return value;
    }

    private void initValue() {
        try {
            value = provider.provide();
        } catch (Exception e) {
            value = fallback;
        }
    }
}
