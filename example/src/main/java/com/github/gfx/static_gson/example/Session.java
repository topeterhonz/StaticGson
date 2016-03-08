package com.github.gfx.static_gson.example;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.github.gfx.static_gson.annotation.JsonSerializable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@JsonObject
@JsonSerializable
public class Session {

    @JsonField
    public int id = 41;

    @JsonField
    public String title = "The Basics of JSON processing libraries";

    @JsonField
    public String description
            = "How to handle JSON in Android Applications with JSON processing libraries.\n"
            + "There are a lot of JSON processing libraries: org.json, Gson, JsonPullParser, Moshi, LoganSquare\n"
            + "Some uses annotation processing while the other uses reflection.\n"
            + "If you want a high performance JSON processing library, you should find annotation processing ones,\n"
            + "because blah blah blah blah...";

    @JsonField
    public Speaker speaker = new Speaker();

    @JsonField
    public long startTime = new Date().getTime();

    @JsonField
    public long endTime = new Date().getTime();

    @JsonField
    public Category category = new Category();

    @JsonField
    public Room room = new Room();

    @JsonField
    public String languageId = "en";

    @JsonField
    public List<Asset> assets = Arrays.asList(
            Asset.create("slide", "http://example.com/slide"),
            Asset.create("movie", "http://example.com/movie"),
            Asset.create("share", "http://example.com/share")
    );

    @JsonField
    public boolean checked = false;

    @JsonSerializable
    @JsonObject
    public static class Speaker {

        @JsonField
        public long id = 42;

        @JsonField
        public String name = "Foo Bar Baz";
    }

    @JsonSerializable
    @JsonObject
    public static class Category {

        @JsonField
        public long id = 43;

        @JsonField
        public String name = "JSON";
    }

    @JsonSerializable
    @JsonObject
    public static class Room {

        @JsonField
        public long id = 44;

        @JsonField
        public String name = "Room A";
    }

    @JsonSerializable
    @JsonObject
    public static class Asset {

        @JsonField
        public String label;

        @JsonField
        public String url;

        public static Asset create(String label, String url) {
            Asset asset = new Asset();

            asset.label = label;
            asset.url = url;

            return asset;
        }
    }
}
