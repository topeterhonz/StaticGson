package com.github.gfx.static_gson;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public class Types {

    public static final ClassName StaticGsonTypeAdapterFactory = ClassName.get(StaticGsonTypeAdapterFactory.class);

    public static final ClassName String = ClassName.get(String.class);

    public static final ClassName Date = ClassName.get(java.util.Date.class);

    public static final ClassName Calendar  = ClassName.get(java.util.Calendar.class);

    public static final ClassName Object = ClassName.get(Object.class);

    public static final ClassName TypeToken = ClassName.get(TypeToken.class);

    public static final ClassName $Gson$Types = ClassName.get($Gson$Types.class);

    public static final ClassName TypeAdapter = ClassName.get(TypeAdapter.class);

    public static ParameterizedTypeName getTypeAdapter(TypeName type) {
        return ParameterizedTypeName.get(TypeAdapter, type.box());
    }
}
