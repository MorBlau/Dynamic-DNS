package com.mblau.ddns.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String toJson(T model) throws JsonProcessingException {
        return objectMapper.writeValueAsString(model);
    }

    public static <T> T toModel(String jsonString, Class<T> type) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, type);
    }
}
