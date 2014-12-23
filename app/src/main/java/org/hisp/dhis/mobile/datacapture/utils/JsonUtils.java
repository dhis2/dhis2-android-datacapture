package org.hisp.dhis.mobile.datacapture.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static JsonObject buildJsonObject(String source) {
        if (source == null) {
            throw new IllegalArgumentException("JSON String object cannot be null");
        }

        JsonReader reader = new JsonReader(new StringReader(source));
        reader.setLenient(true);

        try {
            JsonElement jRawSource = new JsonParser().parse(reader);

            if (jRawSource != null && jRawSource.isJsonObject()) {
                return jRawSource.getAsJsonObject();
            } else {
                throw new RuntimeException("The incoming Json is bad/malicious");
            }
        } catch (JsonParseException e) {
            throw new RuntimeException("The incoming Json is bad/malicious");
        }
    }

    public static JsonArray buildJsonArray(String json) {
        if (json == null) {
            throw new RuntimeException("Cannot parse empty json");
        }

        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);

        try {
            JsonElement jRawSource = new JsonParser().parse(reader);

            if (jRawSource != null && jRawSource.isJsonArray()) {
                return jRawSource.getAsJsonArray();
            } else {
                throw new RuntimeException("The incoming Json is bad/malicious");
            }
        } catch (JsonParseException e) {
            throw new RuntimeException("The incoming Json is bad/malicious");
        }
    }
}
