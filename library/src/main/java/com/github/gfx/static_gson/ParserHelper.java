package com.github.gfx.static_gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;

public class ParserHelper {

    public static Boolean nextRelaxedBoolean(JsonReader reader, boolean nullable, boolean nonNull, Boolean currentValue)
            throws IOException, IllegalAccessException {
        JsonToken token = reader.peek();
        if (token == JsonToken.STRING) {
            String path = reader.getPath();
            String string = reader.nextString();
            if ("true".equalsIgnoreCase(string)) {
                return true;
            } else if ("false".equalsIgnoreCase(string)) {
                return false;
            } else {
                String message = String.format("Expecting true or false but was \"%s\" at %s", string, path);
                RuntimeException ex = new JsonGracefulException(message);
                if (nullable) {
                    Logger.log(ex);
                    return null;
                } else if (!nonNull) {
                    return currentValue;
                } else {
                    throw ex;
                }
            }
        } else if (token == JsonToken.NUMBER) {
            String path = reader.getPath();
            int number = reader.nextInt();
            if (number == 1) {
                return true;
            } else if (number == 0) {
                return false;
            } else {
                String message = String.format("Expecting 1 or 0 but was %d at %s", number, path);
                RuntimeException ex = new JsonGracefulException(message);
                if (nullable) {
                    Logger.log(ex);
                    return null;
                } else if (!nonNull) {
                    return currentValue;
                } else {
                    throw ex;
                }
            }
        } else {
            return reader.nextBoolean();
        }
    }
}
