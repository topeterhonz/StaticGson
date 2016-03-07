package com.github.gfx.static_gson;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.github.gfx.static_gson.annotation.JsonSerializable;

import java.util.Date;

@JsonObject
@JsonSerializable
public class Session {

    @JsonField
    public int id = 41;

    @JsonField
    public String title = "The Basics of JSON";

    @JsonField
    public String description
            = "How to handle JSON in Android Applications with JSON processing libraries.\n"
            + "There are a lot of JSON processing libraries: org.json, Gson, JsonPullParser, Moshi, LoganSquare\n"
            + "Some uses annotation processing while the other uses reflection.\n"
            + "blah blah blah blah...";

    @JsonField
    public long speakerId = 42;

    @JsonField
    public long startTime = new Date().getTime();

    @JsonField
    public long endTime = new Date().getTime();

    @JsonField
    public long categoryId = 43;

    @JsonField
    public long placeId = 44;

    @JsonField
    public String languageId = "en";

    @JsonField
    public Asset slideUrl = Asset.create("slide", "http://example.com/slide");

    @JsonField
    public Asset movieUrl = Asset.create("movie", "http://example.com/movie");

    @JsonField
    public Asset shareUrl = Asset.create("share", "http://example.com/share");

    @JsonField
    public boolean checked = false;

    @JsonObject
    @JsonSerializable
    public static class Asset {

        public String label;

        public String url;

        public static Asset create(String label, String url) {
            Asset asset = new Asset();

            asset.label = label;
            asset.url = url;

            return asset;
        }
    }
}
