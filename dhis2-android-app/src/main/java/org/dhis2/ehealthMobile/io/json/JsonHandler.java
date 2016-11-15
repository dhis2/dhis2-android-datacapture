/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.ehealthMobile.io.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import org.dhis2.ehealthMobile.network.Response;

import java.io.StringReader;
import java.lang.reflect.Type;


public class JsonHandler {
    public static final String PARSING_STATUS_CODE = "parsingStatusCode";
    public static final int PARSING_FAILED_CODE = 445769892;
    public static final int PARSING_OK_CODE = 856725354;

    private JsonHandler() { }

    public static JsonObject buildJsonObject(Response response) throws ParsingException {
        if (response == null || response.getBody() == null) {
            throw new ParsingException("Cannot parse empty response");
        }

        return buildJsonObject(response.getBody());
    }

    public static JsonObject buildJsonObject(String json) throws ParsingException {
        if (json == null) {
            throw new ParsingException("Cannot parse empty json");
        }

        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);

        try {
            JsonElement jRawSource = new JsonParser().parse(reader);

            if (jRawSource != null && jRawSource.isJsonObject()) {
                return jRawSource.getAsJsonObject();
            } else {
                throw new ParsingException("The incoming Json is bad/malicious");
            }
        } catch (JsonParseException e) {
            throw new ParsingException("The incoming Json is bad/malicious");
        }
    }

    public static JsonArray buildJsonArray(String json) throws ParsingException {
        if (json == null) {
            throw new ParsingException("Cannot parse empty json");
        }

        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);

        try {
            JsonElement jRawSource = new JsonParser().parse(reader);

            if (jRawSource != null && jRawSource.isJsonArray()) {
                return jRawSource.getAsJsonArray();
            } else {
                throw new ParsingException("The incoming Json is bad/malicious");
            }
        } catch (JsonParseException e) {
            throw new ParsingException("The incoming Json is bad/malicious");
        }
    }

    public static <T> T fromJson(JsonElement jElement, Class<T> cls) throws ParsingException {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jElement, cls);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new ParsingException("The incoming Json is bad/malicious");
        }
    }

    public static <T> T fromJson(JsonElement jElement, Type type) throws ParsingException {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jElement, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new ParsingException("The incoming Json is bad/malicious");
        }
    }

    public static String getString(JsonObject jObject, String key) throws ParsingException {
        JsonElement jElement = getJsonElement(jObject, key);

        if (jElement.isJsonPrimitive()) {
            return jElement.getAsJsonPrimitive().getAsString();
        } else {
            throw new ParsingException("JsonElement is not JsonPrimitive");
        }
    }

    public static JsonObject getJsonObject(JsonObject jObject, String key) throws ParsingException {
        JsonElement jElement = getJsonElement(jObject, key);
        return getAsJsonObject(jElement);
    }

    public static JsonArray getJsonArray(JsonObject jObject, String key) throws ParsingException {
        JsonElement jElement = getJsonElement(jObject, key);
        return getAsJsonArray(jElement);
    }

    public static JsonArray getAsJsonArray(JsonElement jElement) throws ParsingException {
        if (jElement == null) {
            throw new ParsingException("Wrong params");
        }

        if (jElement.isJsonArray()) {
            return jElement.getAsJsonArray();
        } else {
            throw new ParsingException("JsonElement is not JsonArray");
        }
    }

    public static JsonObject getAsJsonObject(JsonElement jElement) throws ParsingException {
        if (jElement == null) {
            throw new ParsingException("Wrong params");
        }

        if (jElement.isJsonObject()) {
            return jElement.getAsJsonObject();
        } else {
            throw new ParsingException("JsonElement is not JsonObject");
        }
    }

    public static JsonElement getJsonElement(JsonObject jObject, String key) throws ParsingException {
        if (jObject == null || key == null) {
            throw new ParsingException("Wrong params");
        }

        if (jObject.isJsonObject() && jObject.has(key)) {
            return jObject.get(key);
        } else {
            throw new ParsingException("The given jObject is not JsonObject, or doesn't contain given key");
        }
    }
}