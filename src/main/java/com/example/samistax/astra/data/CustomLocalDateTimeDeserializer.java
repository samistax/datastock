package com.example.samistax.astra.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    // create an instance of DateTimeFormatter
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        String str = p.getText();
        try {
            return LocalDateTime.parse(str, dtf);
        } catch (DateTimeParseException e) {
            System.err.println(e);
            return null;
        }
    }
}

