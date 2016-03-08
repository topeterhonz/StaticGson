package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class SerializeNullsTest {

    @JsonSerializable(serializeNulls = true)
    public static class Foo {

        public String foo;

        public Integer bar;
    }

    Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .create();
    }

    @Test
    public void testName() throws Exception {
        assertThat(gson.toJson(new Foo()), is("{}"));
    }
}
