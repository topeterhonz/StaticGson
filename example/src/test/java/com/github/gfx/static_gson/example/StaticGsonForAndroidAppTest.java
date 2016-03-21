package com.github.gfx.static_gson.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.github.gfx.static_gson.StaticGsonTypeAdapterFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@RunWith(JUnit4.class)
public class StaticGsonForAndroidAppTest {

    StaticGsonTypeAdapterFactory typeAdapterFactory;

    Gson gson;

    TypeToken<Session> typeToken;

    @Before
    public void setUp() throws Exception {
        typeAdapterFactory = StaticGsonTypeAdapterFactory.newInstance();
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(typeAdapterFactory)
                .create();
        typeToken = TypeToken.get(Session.class);
    }

    @Test
    public void createTypeAdapter() throws Exception {
        assertThat(typeAdapterFactory.create(gson, typeToken), is(notNullValue()));
    }
}
