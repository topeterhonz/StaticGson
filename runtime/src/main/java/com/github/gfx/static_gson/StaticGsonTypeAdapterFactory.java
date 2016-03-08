package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A {@link TypeAdapterFactory} to handle {@link com.github.gfx.static_gson.annotation.JsonSerializable}.
 */
public class StaticGsonTypeAdapterFactory implements TypeAdapterFactory {

    private static String createTypeAdapterClassName(Class<?> modelType) {
        return modelType.getName() + "$StaticGsonTypeAdapter";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        String name = createTypeAdapterClassName(typeToken.getRawType());
        Class<?> typeAdapterClass;
        try {
            typeAdapterClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }

        Constructor<TypeAdapter<T>> constructor;
        try {
            constructor = (Constructor<TypeAdapter<T>>) typeAdapterClass.getDeclaredConstructor(Gson.class, TypeToken.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Missing constructor constructor(Gson, TypeToken) for " + name, e);
        }
        try {
            return constructor.newInstance(gson, typeToken);
        } catch (IllegalAccessException | InstantiationException | ClassCastException | InvocationTargetException e) {
            throw new RuntimeException("Can't create an instance of " + name, e);
        }
    }
}
