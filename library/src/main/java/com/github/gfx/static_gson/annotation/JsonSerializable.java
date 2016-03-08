package com.github.gfx.static_gson.annotation;


import com.google.gson.FieldNamingPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker to generate static gson type adapters.
 *
 * There are options that is a subset of {@link com.google.gson.GsonBuilder}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface JsonSerializable {

    FieldNamingPolicy fieldNamingPolicy() default FieldNamingPolicy.IDENTITY;

    boolean serializeNulls() default false;
}
