package com.example.springsecurity.util;

import lombok.experimental.UtilityClass;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author Negin Mousavi 5/18/2024 - Saturday
 */
@UtilityClass
public class JsonUtil {
    public List<String> getValuesFromJsonBody(String response, String key1, String key2) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        return List.of(jsonObject.getString(key1), jsonObject.getString(key2));
    }
}
