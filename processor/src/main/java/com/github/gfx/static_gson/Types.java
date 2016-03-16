package com.github.gfx.static_gson;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public class Types {

    public static final String PACKAGE_NAME = "com.github.gfx.static_gson";

    public static final ClassName StaticGsonTypeAdapterFactory
            = ClassName.get(PACKAGE_NAME, "StaticGsonTypeAdapterFactory");

    public static final ClassName String = ClassName.get(String.class);

    public static final ClassName TypeToken = ClassName.get(TypeToken.class);

    public static final ClassName $Gson$Types = ClassName.get($Gson$Types.class);

    public static final ClassName TypeAdapter = ClassName.get(TypeAdapter.class);

    public static ParameterizedTypeName getTypeAdapter(TypeName type) {
        return ParameterizedTypeName.get(TypeAdapter, type);
    }
}
