package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A {@link TypeAdapterFactory} to handle {@link com.github.gfx.static_gson.annotation.JsonSerializable}.
 */
public class StaticGsonTypeAdapterFactory implements TypeAdapterFactory {

    @NonNull
    private static String createTypeAdapterFactoryClassName(@NonNull Class<?> modelType) {
        return modelType.getName() + "$TypeAdapterFactory";
    }

    @Nullable
    public static TypeAdapterFactory loadFactory(@NonNull TypeToken<?> typeToken) {
        String name = createTypeAdapterFactoryClassName(typeToken.getRawType());
        Class<?> factoryClass;
        try {
            factoryClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
        try {
            return (TypeAdapterFactory) factoryClass.newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassCastException e) {
            throw new RuntimeException("Can't create an instance of " + name, e);
        }
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        TypeAdapterFactory factory = loadFactory(typeToken);
        if (factory != null) {
            return factory.create(gson, typeToken);
        } else {
            return null;
        }
    }
}
