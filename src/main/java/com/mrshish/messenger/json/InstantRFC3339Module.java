package com.mrshish.messenger.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantRFC3339Module extends SimpleModule {

    private static final DateTimeFormatter INSTANT_PARSING_FORMATTER = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXX");
    private static final DateTimeFormatter INSTANT_FORMATTING_FORMATTER = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .withZone(ZoneId.of("UTC"));

    /**
     * Note: This module needs to be registered after other possible modules that might try to control `Instant`, such
     * as the JaveTimeModule.
     */
    public InstantRFC3339Module() {
        super();
        addDeserializer(Instant.class, new InstantDeserializer());
        addSerializer(Instant.class, new InstantSerializer());
    }

    public static class InstantSerializer extends JsonSerializer<Instant> {

        @Override
        public void serialize(
            final Instant instant,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider
        ) throws IOException {
            jsonGenerator.writeString(INSTANT_FORMATTING_FORMATTER.format(instant));
        }
    }

    public static class InstantDeserializer extends JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(
            final JsonParser jp,
            final DeserializationContext context
        ) throws IOException {
            final String value = jp.readValueAs(String.class);
            return Instant.from(INSTANT_PARSING_FORMATTER.parse(value));
        }
    }
}
