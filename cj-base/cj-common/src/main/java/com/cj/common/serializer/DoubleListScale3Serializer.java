package com.cj.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class DoubleListScale3Serializer extends JsonSerializer<List<Double>> {
    private static final long serialVersionUID = 1L;

    @Override
    public void serialize(List<Double> value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        if (value != null) {
            double[] newValue = new double[value.size()];
            for (int i = 0; i < value.size(); i++) {
                newValue[i] = new BigDecimal(value.get(i).toString()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            gen.writeArray(newValue, 0, newValue.length);
        }
    }
}
