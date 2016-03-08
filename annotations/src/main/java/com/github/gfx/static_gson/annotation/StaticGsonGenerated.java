package com.github.gfx.static_gson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker annotation indicating it will be dynamically loaded by StaticGsonTypeAdapterFactory.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface StaticGsonGenerated {

    /**
     * @return The class name of the annotation processor.
     */
    String value();
}
