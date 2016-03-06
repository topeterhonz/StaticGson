package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link TypeAdapterFactory} to handle {@link com.github.gfx.static_gson.annotation.JsonSerializable}.
 */
public class StaticGsonTypeAdapterFactory implements TypeAdapterFactory {

    private final Map<TypeToken<?>, TypeAdapterFactory> cache = new HashMap<>();

    @Nullable
    public TypeAdapterFactory loadFactory(TypeToken<?> typeToken) {
        TypeAdapterFactory factory = cache.get(typeToken);
        if (factory == null) {
            String name = typeToken.getRawType().getCanonicalName() + "_TypeAdapterFactory";
            try {
                factory = (TypeAdapterFactory) Class.forName(name).newInstance();
            } catch (ClassNotFoundException e) {
                return null;
            } catch (IllegalAccessException | InstantiationException | ClassCastException e) {
                throw new RuntimeException("Can't create an instance of " + name, e);
            }
            cache.put(typeToken, factory);
        }
        return factory;
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
