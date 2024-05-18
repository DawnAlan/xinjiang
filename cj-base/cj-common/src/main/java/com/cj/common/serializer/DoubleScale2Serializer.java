package com.cj.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class DoubleScale2Serializer extends JsonSerializer<Double> {
    private static final long serialVersionUID = 1L;

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        if (value != null) {
            BigDecimal bigDecimal = new BigDecimal(value.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
            gen.writeNumber(bigDecimal);
        }
    }
}
