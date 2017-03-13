/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.structure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * JSON and String exchange function.
 *
 * @author zoe
 */
public class JSONMessage {
    
    private static final Gson GSON = new GsonBuilder().create();
//    private static final Gson GSON = new Gson();
    public static final JsonParser PARSER = new JsonParser();

    /**
     * String to JSON transform method
     *
     * @param <T> - this is a functional interface and can therefore be used as
     * the assignment target for method reference.
     * @param json - the expected transformed String
     * @param type - the expected class type
     * @return
     */
    public static <T> T fromJSON(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }
    
    public static <T> T fromJSON(JsonElement elem, Class<T> type) {
        return GSON.fromJson(elem, type);
    }

    /**
     * JSON to String transform method.
     *
     * @return
     */
    public String toJSON() {
        return GSON.toJson(this);
    }

    /**
     * JSON to String transfer in console.
     *
     * @param writer
     */
    public void toJSON(Appendable writer) {
        GSON.toJson(this, writer);
    }

    /**
     * override the toString method.
     *
     * @return
     */
    @Override
    public String toString() {
        return toJSON();
    }

}
