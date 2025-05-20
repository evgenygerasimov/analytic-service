package org.site.analyticservice.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeArrayDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int[] array = p.readValueAs(int[].class);
        if (array.length >= 7) {
            return LocalDateTime.of(array[0], array[1], array[2], array[3], array[4], array[5], array[6]);
        } else {
            throw new IOException("Invalid LocalDateTime array format");
        }
    }
}