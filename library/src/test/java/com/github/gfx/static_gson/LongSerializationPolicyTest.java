package com.github.gfx.static_gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class LongSerializationPolicyTest {

    Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create();
    }

    @Test
    public void serializeLong() throws Exception {
        L model = new L();
        model.value = Long.MAX_VALUE;
        assertThat(gson.toJson(model), is("{\"value\":\"" + Long.MAX_VALUE + "\"}"));
    }

    @JsonSerializable
    static class L {

        long value;
    }
}
