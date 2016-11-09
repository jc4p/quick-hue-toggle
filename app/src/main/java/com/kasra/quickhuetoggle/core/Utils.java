package com.kasra.quickhuetoggle.core;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static long LIGHT_VALUE_CHANGE_DEBOUNCE_RATE = 100;

    public static void log(String message) {
        Log.v("QuickHueToggle", message);
    }

    public static void log(String message, String tag) {
        Log.v(tag, message);
    }

    public static class DictionaryOfItemsDeserializer<T> implements JsonDeserializer<HashMap<String, T>> {
        private Class<T> itemClass;
        private Class<? extends HashMap<String, T>> enclosingClass;

        public DictionaryOfItemsDeserializer(Class<T> itemClass, Class<? extends HashMap<String, T>> enclosingClass) {
            this.itemClass = itemClass;
            this.enclosingClass = enclosingClass;
        }

        @Override
        public HashMap<String, T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull())
                return null;
            else if (json.isJsonObject())
                return handleObject(json.getAsJsonObject(), context);

            return null;
        }

        HashMap<String, T> handleObject(JsonObject json, JsonDeserializationContext context) {
            HashMap<String, T> map;
            try {
                map = enclosingClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
            for(Map.Entry<String, JsonElement> entry : json.entrySet())
                map.put(entry.getKey(), context.deserialize(entry.getValue(), itemClass));
            return enclosingClass.cast(map);
        }
    }
}
