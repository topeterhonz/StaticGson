package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class SerializeNullsTest {

    Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .serializeNulls()
                .create();
    }

    @Test
    public void serializedNullsIsTrue() throws Exception {
        assertThat(gson.toJson(new SerializedNullsIsTrue()), is("{\"foo\":null,\"bar\":null}"));
    }

    @Test
    public void serializedNullsIsFalse() throws Exception {
        assertThat(gson.toJson(new SerializedNullsIsFalse()), is("{}"));
    }

    @JsonSerializable(serializeNulls = true)
    public static class SerializedNullsIsTrue {

        public String foo;

        public Integer bar;
    }

    @JsonSerializable(serializeNulls = false)
    public static class SerializedNullsIsFalse {

        public String foo;

        public Integer bar;
    }
}
