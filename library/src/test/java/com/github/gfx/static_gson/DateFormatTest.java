package com.github.gfx.static_gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class DateFormatTest {

    Gson gson;

    @Before
    public void setUp() throws Exception {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(null);

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .create();
    }

    @Test
    public void testSerializeDate() throws Exception {
        long t = 1458049044439L;
        Full model = new Full();
        model.date = new Date(t);
        assertThat(gson.toJson(model), is("{\"Date\":\"2016-03-15T13:37:24+0000\"}"));
    }

    @JsonSerializable(fieldNamingPolicy = FieldNamingPolicy.UPPER_CAMEL_CASE)
    static class Full {

        Date date;
    }
}
