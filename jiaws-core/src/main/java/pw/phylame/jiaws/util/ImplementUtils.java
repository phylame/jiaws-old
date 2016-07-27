package pw.phylame.jiaws.util;

public final class ImplementUtils {
    private ImplementUtils() {
    }

    /**
     * Throws an <code>IllegalStateException<code> when invoking deprecated
     * method.
     * 
     * @return nothing returned(used for invoker)
     * @throws IllegalStateException
     */
    public static <T> T raiseForDeprecated() throws IllegalStateException {
        String invokerName = Thread.currentThread().getStackTrace()[2].getMethodName();
        throw new IllegalStateException(String.format("Method '%s' is deprecated", invokerName));
    }

    /**
     * Throws an <code>IllegalStateException<code> indicate a TODO
     * 
     * @param message
     *            the error message
     * @return nothing returned(used for invoker)
     * @throws IllegalStateException
     */
    public static <T> T raiseForTodo(String message) throws IllegalStateException {
        throw new IllegalStateException(message);
    }
}
