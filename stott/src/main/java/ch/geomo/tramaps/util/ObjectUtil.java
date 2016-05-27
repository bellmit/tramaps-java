/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.util;

public final class ObjectUtil {

    private ObjectUtil() {
    }

    /**
     * Throws a {@link ClassCastException} if given object cannot be casted to given {@link Class} type.
     */
    public static void checkClass(Class<?> cls, Object... objs) {
        for (Object obj : objs) {
            if (obj != null && !cls.isInstance(obj)) {
                throw new ClassCastException();
            }
        }
    }

}
