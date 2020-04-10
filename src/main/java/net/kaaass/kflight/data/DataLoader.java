package net.kaaass.kflight.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kaaass.kflight.data.entry.EntryFlight;
import net.kaaass.kflight.util.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * 数据加载
 */
public class DataLoader {

    /**
     * 从文件名加载航班数据
     */
    public static void loadFlightFromJsonFile(String filename) throws IOException {
        var jsonStr = FileUtils.readAll(filename);
        loadFlightFromJson(jsonStr);
    }

    /**
     * 从资源名加载航班数据
     */
    public static void loadFlightFromJsonResource(String resource) throws IOException {
        var stream = DataLoader.class.getResourceAsStream(resource);
        if (stream == null)
            throw new FileNotFoundException("No such resource: " + resource);
        var jsonStr = FileUtils.readAll(stream);
        loadFlightFromJson(jsonStr);
    }

    /**
     * 从 Json 字符串加载航班数据
     */
    public static void loadFlightFromJson(String json) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var list = mapper.readValue(json,new TypeReference<List<EntryFlight>>(){});
        list.forEach(FlightManager::addEntry);
    }
}
