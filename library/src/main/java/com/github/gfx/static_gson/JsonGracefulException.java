package com.github.gfx.static_gson;

import com.google.gson.JsonParseException;

public class JsonGracefulException extends JsonParseException {
    public JsonGracefulException(String msg) {
        super(msg);
    }

    public JsonGracefulException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JsonGracefulException(Throwable cause) {
        super(cause);
    }
}
