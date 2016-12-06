package com.github.gfx.static_gson;

import com.github.gfx.static_gson.annotation.JsonSerializable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeserializeNullsTest {

    Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .serializeNulls()
                .create();
    }

    @Test
    public void deserializeNulls() throws Exception {
        String json = "{\"foo\":null,\"bar\":null,\"baz\":null,\"qux\":null,\"quux\":null,\"hoge\":null,\"fuga\":null,\"piyo\":null}";
        DeserializeNullValue deserialized = gson.fromJson(json, DeserializeNullValue.class);
        assertThat(deserialized.foo, nullValue());
        assertThat(deserialized.bar, nullValue());
        assertThat(deserialized.baz, nullValue());
        assertThat(deserialized.qux, nullValue());
        assertThat(deserialized.quux, nullValue());
        assertThat(deserialized.hoge, nullValue());
        assertThat(deserialized.fuga, nullValue());
        assertThat(deserialized.piyo, nullValue());
    }

    @JsonSerializable
    public static class DeserializeNullValue {

        public String foo;

        public Integer bar;

        public Boolean baz;

        public Double qux;
        public Long quux;
        public Byte hoge;
        public Short fuga;
        public Float piyo;
    }

}
