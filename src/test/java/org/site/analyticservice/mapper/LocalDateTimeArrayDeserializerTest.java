package org.site.analyticservice.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalDateTimeArrayDeserializerTest {

    private final LocalDateTimeArrayDeserializer deserializer = new LocalDateTimeArrayDeserializer();

    @Test
    void deserialize_shouldReturnLocalDateTime_whenValidArray() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        int[] dateTimeArray = {2024, 6, 15, 14, 30, 25, 123456789};
        when(parser.readValueAs(int[].class)).thenReturn(dateTimeArray);

        LocalDateTime result = deserializer.deserialize(parser, context);

        assertThat(result)
                .isEqualTo(LocalDateTime.of(2024, 6, 15, 14, 30, 25, 123456789));

        verify(parser, times(1)).readValueAs(int[].class);
    }

    @Test
    void deserialize_shouldThrowIOException_whenArrayLengthLessThan7() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        int[] invalidArray = {2024, 6, 15, 14, 30}; // length 5 < 7
        when(parser.readValueAs(int[].class)).thenReturn(invalidArray);

        assertThatThrownBy(() -> deserializer.deserialize(parser, context))
                .isInstanceOf(IOException.class)
                .hasMessage("Invalid LocalDateTime array format");

        verify(parser, times(1)).readValueAs(int[].class);
    }
}
