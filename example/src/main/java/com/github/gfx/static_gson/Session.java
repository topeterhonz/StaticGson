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
            = "How to handle JSON in Android Applications with JSON processing libraries";

    @JsonField
    public long speakerId = 42;

    @JsonField
    public long stime = new Date().getTime();

    @JsonField
    public long etime = new Date().getTime();

    @JsonField
    public long categoryId = 43;

    @JsonField
    public long placeId = 44;

    @JsonField
    public String languageId = "ja";

    @JsonField
    public String slideUrl = "http://example.com/slide";

    @JsonField
    public String movieUrl = "http://example.com/movie";

    @JsonField
    public String shareUrl = "http://example.com/share";

    @JsonField
    public boolean checked = false;
}
