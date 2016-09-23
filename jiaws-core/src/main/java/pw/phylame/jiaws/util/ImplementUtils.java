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

import lombok.val;

public final class ImplementUtils {
    private ImplementUtils() {
    }

    /**
     * Throws an <code>IllegalStateException<code> when invoking deprecated method.
     *
     * @return nothing returned(used for invoker)
     * @throws IllegalStateException
     */
    public static <T> T raiseForDeprecated() throws IllegalStateException {
        val invoker = Thread.currentThread().getStackTrace()[2].getMethodName();
        throw Exceptions.forIllegalState("Method '%s' is deprecated", invoker);
    }

    /**
     * Throws an <code>IllegalStateException<code> indicate a TODO.
     *
     * @param message
     *        the error message
     * @return nothing returned(used for invoker)
     * @throws IllegalStateException
     */
    public static <T> T raiseForTodo(String message) throws IllegalStateException {
        throw new IllegalStateException(message);
    }

    /**
     * Throws an <code>IllegalStateException<code> when developing method.
     *
     * @return nothing returned(used for invoker)
     */
    public static <T> T raiseForImpl() {
        val stacks = Thread.currentThread().getStackTrace();
        throw Exceptions.forIllegalState("Method '%s.%s' is under development",
                stacks[2].getClassName(),
                stacks[2].getMethodName());
    }
}
