package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import com.github.gfx.static_gson.model.Book;
import com.github.gfx.static_gson.model.ModelWithBasicTypes;
import com.github.gfx.static_gson.model.ModelWithNumerics;
import com.github.gfx.static_gson.model.ModelWithSingleValue;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @see StaticGsonTypeAdapterFactory
 */
public class StaticGsonTest {

    Gson gson;

    TypeAdapterFactory typeAdapterFactory;

    @Before
    public void setUp() throws Exception {
        typeAdapterFactory = new StaticGsonTypeAdapterFactory();
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(typeAdapterFactory)
                .create();
    }

    @Test
    public void createTypeAdapter() throws Exception {
        assertThat(typeAdapterFactory.create(gson, TypeToken.get(Book.class)), is(notNullValue()));
        assertThat(typeAdapterFactory.create(gson, TypeToken.get(Book.Author.class)), is(notNullValue()));
        assertThat(typeAdapterFactory.create(gson, TypeToken.get(StaticGsonTest.class)), is(nullValue()));
    }

    @Test
    public void reloadBook() throws Exception {
        Book book = new Book();
        book.title = "About JSON";
        book.authors = Arrays.asList(Book.Author.create("foo"), Book.Author.create("bar"));

        String serialized = gson.toJson(book);
        Book deserialized = gson.fromJson(serialized, Book.class);

        assertThat(deserialized, is(book));
    }

    @Test
    public void reloadAuthor() throws Exception {
        Book.Author author = Book.Author.create("foo bar");

        String serialized = gson.toJson(author);
        Book.Author deserialized = gson.fromJson(serialized, Book.Author.class);

        assertThat(deserialized, is(author));
    }

    @Test
    public void reloadModelWithBasicTypes() throws Exception {
        ModelWithBasicTypes model = new ModelWithBasicTypes();

        String serialized = gson.toJson(model);
        ModelWithBasicTypes deserialized = gson.fromJson(serialized, ModelWithBasicTypes.class);

        assertThat(deserialized, is(model));
    }

    @Test
    public void deserializeNumericFromString() throws Exception {
        ModelWithNumerics model = gson.fromJson("{\"value\": \"42\"}", ModelWithNumerics.class);
        assertThat(model.value, is(42));
    }

    @Test
    public void deserializeNumericFromNull() throws Exception {
        ModelWithNumerics model = gson.fromJson("{\"value\": null}", ModelWithNumerics.class);
        assertThat(model.value, is(0));
    }

    @Test
    public void serializeNullValue() throws Exception {
        ModelWithSingleValue model = new ModelWithSingleValue();
        assertThat(gson.toJson(model), is("{}"));
    }

    @Test
    public void deserializeNullValue() throws Exception {
        assertThat(gson.fromJson("{}", ModelWithSingleValue.class).value, is(nullValue()));
    }
}
