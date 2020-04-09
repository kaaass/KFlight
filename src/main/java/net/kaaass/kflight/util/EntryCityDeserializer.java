package net.kaaass.kflight.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.kaaass.kflight.data.CityManager;
import net.kaaass.kflight.data.entry.EntryCity;

import java.io.IOException;

public class EntryCityDeserializer extends JsonDeserializer<EntryCity> {
    @Override
    public EntryCity deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String name = p.getValueAsString();
        return CityManager.findByName(name).orElseGet(() -> {
            if (name == null)
                return null;
            var newCity = new EntryCity(name);
            CityManager.addEntry(newCity);
            return newCity;
        });
    }
}
