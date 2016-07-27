package pw.phylame.jiaws.servlet.impl;

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
