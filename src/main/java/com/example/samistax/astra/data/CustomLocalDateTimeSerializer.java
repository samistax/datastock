package com.example.samistax.astra.data;
//import required classes and package if any
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CustomLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    // create an instance of DateTimeFormatter
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // default constructor
    public CustomLocalDateTimeSerializer() {
        this(null);
    }

    // parameterized constructor
    public CustomLocalDateTimeSerializer(Class t) {
        super(t);
    }

    // override serialize() method
    @Override
    public void serialize (LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        generator.writeString(dtf.format(value));
    }
}