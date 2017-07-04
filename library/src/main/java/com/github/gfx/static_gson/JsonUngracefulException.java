package com.github.gfx.static_gson;

import com.google.gson.JsonParseException;

public class JsonUngracefulException extends JsonParseException {
    public JsonUngracefulException(Throwable cause) {
        super(cause);
    }
}
