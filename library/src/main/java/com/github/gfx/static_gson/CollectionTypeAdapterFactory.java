// forked from Gson so we can gracefully ignore failed list items
package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapt a homogeneous collection of objects.
 */
public final class CollectionTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;

    public CollectionTypeAdapterFactory() {
        final Map<Type, InstanceCreator<?>> instanceCreators
                = new HashMap<Type, InstanceCreator<?>>();
        this.constructorConstructor = new ConstructorConstructor(instanceCreators);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();

        Class<? super T> rawType = typeToken.getRawType();
        if (!Collection.class.isAssignableFrom(rawType)) {
            return null;
        }

        Type elementType = $Gson$Types.getCollectionElementType(type, rawType);
        // Only use this specialised collection adapter for @JsonSerializable StaticGson types.
        // For other types we can't be sure the reader will be left in a position to safely read the next element in the array.
        // The reader could be left anywhere the exception is thrown
        if (!(elementType instanceof Class) || !StaticGsonTypeAdapterFactory.isStaticGsonType(((Class) elementType).getName())) {
            return null;
        }

        TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
        ObjectConstructor<T> constructor = constructorConstructor.get(typeToken);

        @SuppressWarnings({"unchecked", "rawtypes"}) // create() doesn't define a type parameter
                TypeAdapter<T> result = new Adapter(gson, elementType, elementTypeAdapter, constructor);
        return result;
    }

    private static final class Adapter<E> extends TypeAdapter<Collection<E>> {
        private final TypeAdapter<E> elementTypeAdapter;
        private final ObjectConstructor<? extends Collection<E>> constructor;

        public Adapter(Gson context, Type elementType,
                       TypeAdapter<E> elementTypeAdapter,
                       ObjectConstructor<? extends Collection<E>> constructor) {
            this.elementTypeAdapter = elementTypeAdapter;
            this.constructor = constructor;
        }

        @Override
        public Collection<E> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            Collection<E> collection = constructor.construct();
            in.beginArray();
            while (in.hasNext()) {
                try {
                    E instance = elementTypeAdapter.read(in);
                    collection.add(instance);
                } catch (Exception ex) {
                    Logger.log(ex);
                }
            }
            in.endArray();
            return collection;
        }

        @Override
        public void write(JsonWriter out, Collection<E> collection) throws IOException {
            if (collection == null) {
                out.nullValue();
                return;
            }

            out.beginArray();
            for (E element : collection) {
                elementTypeAdapter.write(out, element);
            }
            out.endArray();
        }
    }
}
