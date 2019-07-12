package org.torc.robot2019.annotation_scanner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnDisabled {
    /**
     * The identifier for determining which item should be instantiated
     */
    String name();
}