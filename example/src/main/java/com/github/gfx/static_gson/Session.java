package com.github.gfx.static_gson;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import java.util.Date;

@JsonSerializable
public class Session {

    public int id = 41;

    public String title = "The Basics of JSON";

    public String description
            = "How to handle JSON in Android Applications with JSON processing libraries";

    public long speakerId = 42;

    public long stime = new Date().getTime();

    public long etime = new Date().getTime();

    public long categoryId = 43;

    public long placeId = 44;

    public String languageId = "ja";

    public String slideUrl = "http://example.com/slide";

    public String movieUrl = "http://example.com/movie";

    public String shareUrl = "http://example.com/share";

    public boolean checked = false;
}
