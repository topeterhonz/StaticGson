package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.github.gfx.static_gson.model.Author;
import com.github.gfx.static_gson.model.Book;
import com.github.gfx.static_gson.model.ModelWithBasicTypes;
import com.github.gfx.static_gson.model.ModelWithNumerics;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @see com.github.gfx.static_gson.model.Book_TypeAdapterFactory
 * @see com.github.gfx.static_gson.model.Author_TypeAdapterFactory
 * @see com.github.gfx.static_gson.model.ModelWithBasicTypes_TypeAdapterFactory
 */
public class StaticGsonTest {

    Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new StaticGsonTypeAdapterFactory())
                .create();
    }

    @Test
    public void reloadBook() throws Exception {
        Book book = new Book();
        book.title = "About JSON";
        book.authors = Arrays.asList(Author.create("foo"), Author.create("bar"));

        String serialized = gson.toJson(book);
        Book deserialized = gson.fromJson(serialized, Book.class);

        assertThat(deserialized, is(book));
    }

    @Test
    public void reloadAuthor() throws Exception {
        Author author = Author.create("foo bar");

        String serialized = gson.toJson(author);
        Author deserialized = gson.fromJson(serialized, Author.class);

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
}
