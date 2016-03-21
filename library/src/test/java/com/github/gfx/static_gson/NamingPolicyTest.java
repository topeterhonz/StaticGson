package com.github.gfx.static_gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class NamingPolicyTest {

    Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .create();
    }

    @Test
    public void namingPolicyLCWU() throws Exception {
        String json = "{\"foo_bar_baz\":42}";

        assertThat(gson.toJson(new LCWU(42)), is(json));
        assertThat(gson.fromJson(json, LCWU.class).fooBarBaz, is(42));
    }

    @Test
    public void namingPolicyLCWD() throws Exception {
        String json = "{\"foo-bar-baz\":42}";

        assertThat(gson.toJson(new LCWD(42)), is(json));
        assertThat(gson.fromJson(json, LCWD.class).fooBarBaz, is(42));
    }

    @Test
    public void namingPolicyUCC() throws Exception {
        String json = "{\"FooBarBaz\":42}";

        assertThat(gson.toJson(new UCC(42)), is(json));
        assertThat(gson.fromJson(json, UCC.class).fooBarBaz, is(42));
    }

    @Test
    public void namingPolicyUCCWS() throws Exception {
        String json = "{\"Foo Bar Baz\":42}";

        assertThat(gson.toJson(new UCCWS(42)), is(json));
        assertThat(gson.fromJson(json, UCCWS.class).fooBarBaz, is(42));
    }

    @JsonSerializable(fieldNamingPolicy = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    public static class LCWU {

        public int fooBarBaz; // to be foo_bar_baz

        public LCWU() {

        }

        public LCWU(int value) {
            fooBarBaz = value;
        }
    }

    @JsonSerializable(fieldNamingPolicy = FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
    public static class LCWD {

        public int fooBarBaz; // to be foo-bar-baz

        public LCWD() {

        }

        public LCWD(int value) {
            fooBarBaz = value;
        }
    }

    @JsonSerializable(fieldNamingPolicy = FieldNamingPolicy.UPPER_CAMEL_CASE)
    public static class UCC {

        public int fooBarBaz; // to be FooBarBaz

        public UCC() {

        }

        public UCC(int value) {
            fooBarBaz = value;
        }
    }

    @JsonSerializable(fieldNamingPolicy = FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES)
    public static class UCCWS {

        public int fooBarBaz; // to be FooBarBaz

        public UCCWS() {

        }

        public UCCWS(int value) {
            fooBarBaz = value;
        }
    }

}
