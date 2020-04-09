package net.kaaass.kflight.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.kaaass.kflight.data.entry.EntryCity;

import java.io.IOException;

public class EntryCitySerializer extends JsonSerializer<EntryCity> {
    @Override
    public void serialize(EntryCity value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null)
            gen.writeNull();
        else
            gen.writeString(value.getName());
    }
}
