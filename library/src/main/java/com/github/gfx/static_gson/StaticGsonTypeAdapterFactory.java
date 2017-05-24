package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link TypeAdapterFactory} to handle {@link com.github.gfx.static_gson.annotation.JsonSerializable}.
 */
public class StaticGsonTypeAdapterFactory implements TypeAdapterFactory {

    private final ConstructorConstructor constructorConstructor;

    public StaticGsonTypeAdapterFactory() {
        final Map<Type, InstanceCreator<?>> instanceCreators
                = new HashMap<Type, InstanceCreator<?>>();
        this.constructorConstructor = new ConstructorConstructor(instanceCreators);
    }

    public static StaticGsonTypeAdapterFactory newInstance() {
        return new StaticGsonTypeAdapterFactory();
    }

    public static String getTypeAdapterFactoryName(String modelClassName) {
        return modelClassName + "_StaticGsonTypeAdapter";
    }

    public static boolean isStaticGsonType(String typeName) {
        String name = StaticGsonTypeAdapterFactory.getTypeAdapterFactoryName(typeName);
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        String name = getTypeAdapterFactoryName(typeToken.getRawType().getName());
        Class<?> typeAdapterClass;
        try {
            typeAdapterClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }

        Constructor<TypeAdapter<T>> constructor;
        try {
            constructor = (Constructor<TypeAdapter<T>>) typeAdapterClass.getDeclaredConstructor(Gson.class, TypeToken.class, ObjectConstructor.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Missing constructor constructor(Gson, TypeToken, ObjectConstructor) for " + name, e);
        }
        try {
            ObjectConstructor<T> objectConstructor = constructorConstructor.get(typeToken);
            return constructor.newInstance(gson, typeToken, objectConstructor);
        } catch (IllegalAccessException | InstantiationException | ClassCastException | InvocationTargetException e) {
            throw new RuntimeException("Can't create an instance of " + name, e);
        }
    }
}
